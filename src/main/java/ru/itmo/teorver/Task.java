package ru.itmo.teorver;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import lombok.*;

import java.io.IOException;
import java.util.*;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.util.Collections.max;
import static java.util.Collections.min;

@Getter
@Setter
@RequiredArgsConstructor
public class Task {
    public final List<Double> elements;

    public List<Double> getVariationSeries() {
        // sorted collection
        return elements.stream().sorted().toList();
    }

    public List<Double> getExtremeValues() {
        // [minimum, maximum]
        return List.of(min(elements), max(elements));
    }

    public Double getSampleRange() {
        // maximum - minimum
        List<Double> extremeValues = getExtremeValues();
        Double minimum = extremeValues.get(0);
        Double maximum = extremeValues.get(1);
        return maximum - minimum;
    }

    public Double getExpectedValue() {
        // MX = sum(x * n_x)
        return elements.stream()
                .mapToDouble(x -> elementFrequency(x) * x)
                .sum();
    }

    private Double elementFrequency(Double element) {
        // n = n_i / n
        return ((double) elements.stream()
                .filter(x -> Objects.equals(x, element))
                .count()) / elements.size();
    }

    public Double getDispersion() {
        // DX = MX^2 - (MX)^2
        Double expectedSquareValue = elements.stream()
                .mapToDouble(x -> elementFrequency(x) * pow(x, 2))
                .sum();
        Double squaredExpectedValue = pow(getExpectedValue(), 2);
        return expectedSquareValue - squaredExpectedValue;
    }

    public Double getStandardDeviation() {
        // oX = sqrt(DX)
        return sqrt(getDispersion());
    }

    public Map<Double, Double> getEmpiricalDistributionFunction() {
        // F*_n(x) = p*{X < x}
        // F*_n(x) = n_x / n
        TreeMap<Double, Double> result = new TreeMap<>(); // TreeMap для удобства расположения ключей в порядке возрастания

        for (Double currentElement : elements) {
            result.put(currentElement, calculateFrequencyFuncForElement(currentElement));
        }
        return result;
    }

    private Double calculateFrequencyFuncForElement(Double element) {
        double quantity = (double) elements.stream()
                .filter(x -> x < element)
                .count();
        // считаем их частость (относительную частоту)
        return quantity / elements.size();
    }

    private Integer getElementQuantity(Double element) {
        return Math.toIntExact(elements.stream().filter(x -> x.equals(element)).count());
    }

    public List<Double> getModes() {
        // Ищем максимальную частоту
        Integer maxN = max(elements.stream().map(this::getElementQuantity).toList());
        // Оставляем только те значения, которые имеют максимальную частоту
        return getVariationSeries().stream().filter(x -> getElementQuantity(x).equals(maxN)).toList();
    }

    public Double getMedian() {
        if (elements.size() % 2 == 0) {
            return (getVariationSeries().get(elements.size() / 2 - 1) + getVariationSeries().get(elements.size() / 2)) / 2;
        } else {
            return getVariationSeries().get(elements.size() / 2 - 1);
        }
    }

    public Double calculateElementsInRange(Double x0, Double x1) {
        return (double) elements.stream().filter(x -> x0 <= x && x < x1).count();
    }

    public List<List<Double>> getStatisticSeries() {
        Double m = Math.ceil(1 + Math.log(elements.size()) / Math.log(2)); // число интервалов
        double h = getSampleRange() / m;
        Double x0 = min(elements);
        List<List<Double>> result = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            Double _x0 = x0 + h * i;
            Double _x1 = x0 + h * (i + 1);
            result.add(List.of(_x0, _x1, calculateElementsInRange(_x0, _x1)));
        }
        return result;
    }

    public void printEmpiricalDistributionFunction(Map<Double, Double> func) {
        Double lastValue = null;
        for (Double key : func.keySet()) {
            if (lastValue == null) {
                System.out.printf("\t-∞ < X <= %.2f\t||\t", key);
            } else {
                System.out.printf("%.2f < X <= %.2f\t||\t", lastValue, key);
            }
            lastValue = key;
        }
        System.out.printf("%.2f < X < +∞\t||\t", lastValue);
        System.out.print("\n\t");

        for (Double value : func.values()) {
            System.out.printf("\t%.2f\t\t||\t\t", value);
        }
        System.out.printf("\t%.2f\t||\t\t\t", 1.0);
        System.out.println();
    }

    public void printStatisticSeries(List<List<Double>> statistic) {
        List<List<Double>> ranges = statistic.stream().map(x -> List.of(x.get(0), x.get(1))).toList();
        List<Double> values = statistic.stream().map(x -> x.get(2)).toList();

        for (List<Double> range : ranges) {
            Double start = range.get(0);
            Double end = range.get(1);
            System.out.printf("%.2f <= X < %.2f\t||\t", start, end);
        }
        System.out.println();
        for (Double value : values) {
            System.out.printf("\t%.2f\t\t\t||\t", value);
        }
        System.out.println();


    }

    public void showFrequency() throws PythonExecutionException, IOException {
        Plot plt = Plot.create();
        plt.subplot(2, 2, 1);
        List<Double> x1 = getStatisticSeries().stream().map(x -> (x.get(0) + x.get(1)) / 2).toList();
        List<Double> y1 = getStatisticSeries().stream().map(x -> x.get(2)).toList();
        plt.plot()
                .add(x1, y1)
                .linestyle("-");
        plt.xlabel("x");
        plt.ylabel("n_i");
        plt.title("Полигон частот");

        plt.subplot(2, 2, 2);
        List<Double> x2 = getStatisticSeries().stream().map(x -> (x.get(0) + x.get(1)) / 2).toList();
        List<Double> y2 = getStatisticSeries().stream().map(x -> x.get(2) / elements.size()).toList();
        plt.plot()
                .add(x2, y2)
                .linestyle("-");
        plt.xlabel("x");
        plt.ylabel("p_i");
        plt.title("Полигон частостей");

        plt.subplot(2, 2, 3);
        List<Double> x3 = getVariationSeries();
        List<Double> y3 = getVariationSeries().stream().mapToDouble(this::calculateFrequencyFuncForElement).boxed().toList();
        plt.plot().add(List.of(-100, min(x3)), List.of(0, 0), "r-");
        plt.plot().add(List.of(max(x3), 1000000000), List.of(1, 1), "r-");
        plt.plot().add(List.of(max(x3)), List.of(1), "r<");
        plt.plot().add(List.of(min(x3)), List.of(0), "ro");

        // Рисуем основную функцию
        for (int i = 0; i < elements.size() - 1; i++) {
            Double _x1 = x3.get(i);
            Double _x2 = x3.get(i + 1);
            Double _y1 = y3.get(i + 1);
            Double _y2 = y3.get(i + 1);
            plt.plot()
                    .add(List.of(_x1, _x2), List.of(_y1, _y2), "r-");
            plt.plot()
                    .add(List.of(_x1), List.of(_y1), "r<");
            plt.plot()
                    .add(List.of(_x2), List.of(_y2), "ro");
        }
        plt.xlabel("x");
        plt.ylabel("y");
        plt.xlim(min(getVariationSeries()) - 0.5, max(getVariationSeries()) + 0.5);
        plt.ylim(-0.1, 1.1);
        plt.title("Эмпирическая функция распределения");

        plt.subplot(2, 2, 4);
        List<List<Double>> data = getStatisticSeries();
        List<Double> x4 = new ArrayList<>(data.stream().map(x -> x.get(0)).toList());
        x4.add(data.get(data.size() - 1).get(1));
        List<Double> y4 = data.stream().map(x -> x.get(2)).toList();
        plt.hist()
                .add(getVariationSeries()).bins(x4);
        plt.title("Гистограмма частот статистического ряда");
        plt.show();
    }
}

