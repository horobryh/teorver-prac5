package ru.itmo.teorver;

import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import java.io.IOException;
import java.util.List;

public class App {
    public static void main(String[] args) throws PythonExecutionException, IOException {
        List<Double> elements = List.of(1.07, -1.02, -1.18, 1.34, 1.69, 0.31, 0.48, 0.11, 0.92, 0.04, 1.42, -1.59, -0.08, -0.21, 0.65, 0.55, 0.66, 1.22, 0.46, 0.82);
        Task task = new Task(elements);

        System.out.print("Вариационный ряд:\t");
        System.out.println(task.getVariationSeries());
                
        System.out.print("Экстремальные значения:\t");
        System.out.println(task.getExtremeValues());
                
        System.out.print("Размах выборки:\t");
        System.out.printf("%.2f\n", task.getSampleRange());
                
        System.out.print("Оценка математического ожидания:\t");
        System.out.printf("%.2f\n", task.getExpectedValue());
                
        System.out.print("Оценка дисперсии:\t");
        System.out.printf("%.2f\n", task.getDispersion());
                
        System.out.print("Оценка среднеквадратического отклонения:\t");
        System.out.printf("%.2f\n", task.getStandardDeviation());
                
        System.out.print("Мода ряда:\t");
        System.out.println(task.getModes());
                
        System.out.print("Медиана ряда:\t");
        System.out.printf("%.2f\n", task.getMedian());
                
        System.out.println("Статистический ряд:\t");
        task.printStatisticSeries(task.getStatisticSeries());
                
        System.out.println("Эмпирическая функция распределения:\t");
        task.printEmpiricalDistributionFunction(task.getEmpiricalDistributionFunction());
        System.out.println();
        System.out.println();
        System.out.println();
        task.showFrequency();
    }
}
