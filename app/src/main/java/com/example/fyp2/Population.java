package com.example.fyp2;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class Population {
    private ArrayList<Individual> individuals;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Population(int size, ArrayList<TaskList> myTask){
        individuals = new ArrayList<Individual>(size);
        IntStream.range(0, size).forEach(x->individuals.add(new Individual(myTask).initialize()));
    }
    public ArrayList<Individual> getSchedules(){
        return this.individuals;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Population sortByFitness(){
        individuals.sort((schedule1,schedule2)->{
            int returnValue=0;
            if(schedule1.getFitness() > schedule2.getFitness()) returnValue = -1;
            else if(schedule1.getFitness() < schedule2.getFitness())returnValue = 1;
            return returnValue;
        });
        return this;
    }
}
