package com.example.fyp2;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class Individual {

    private boolean isFitnessChanged = true;
    private double fitness = -1;
    private int numbOfConflicts = 0;
    private ArrayList<TaskList> sortTask;
    private int i;

    public Individual(ArrayList<TaskList> myTask) {
        sortTask = myTask;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public Individual initialize() {
        i = 0;
        new ArrayList<TaskList>(sortTask).forEach(task -> {
            int temp = (int) (sortTask.size() * Math.random());
            System.out.println("aaaaaaaaaaaaaaa" + temp + " i is" + i);
            TaskList tempData = sortTask.get(temp);
            sortTask.set(temp, sortTask.get(i));
            sortTask.set(i, tempData);
            i++;
        });
        return this;
    }

    public int getNumbOfConflicts() {
        return numbOfConflicts;
    }

    public ArrayList<TaskList> getTask() {
        isFitnessChanged = true;
        return sortTask;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public double getFitness() {
        if (isFitnessChanged == true) {
            fitness = calculateFitness();
            isFitnessChanged = false;
        }
        return fitness;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private double calculateFitness() {
        numbOfConflicts = 0;

        sortTask.forEach(x -> {
            sortTask.stream().filter(y -> sortTask.indexOf(y) >= sortTask.indexOf(x)).forEach(y -> {

                if (x.getTaskID() != y.getTaskID()) {
                    if (x.getMyDate().compareTo(y.getMyDate()) > 0) {
                        numbOfConflicts += 1;
                    } else if (x.getMyDate().compareTo(y.getMyDate()) == 0) {
                        if (x.getMyTime().compareTo(y.getMyTime()) == 0) {
                            if (x.getPriority() < y.getPriority()) {
                                numbOfConflicts += 1;
                            }
                        } else if (x.getMyTime().compareTo(y.getMyTime()) > 0) {
                            numbOfConflicts += 1;
                        }
                    }
                }

            });
        });
        return 1 / (double) (numbOfConflicts + 1);

    }
}
