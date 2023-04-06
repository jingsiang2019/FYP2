package com.example.fyp2;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class GeneticAlgo {
    private static final double MUTATION_RATE = 0.005;
    private static final double CROSSOVER_RATE = 0.9;
    private static final int TOURNAMENT_SELECTION_SIZE = 3;
    private static final int NUMB_OF_ELITE_SCHEDULES = 1;
    private ArrayList<TaskList> myTask;


    public GeneticAlgo(ArrayList<TaskList> myTask){
        this.myTask=myTask;
    }

    public ArrayList<TaskList> getTaskList(){
        return myTask;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Population evolve(Population population){
        return mutatePopulation(crossoverPopulation(population));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    Population crossoverPopulation(Population population){
        Population crossoverPopulation = new Population(population.getSchedules().size(),myTask);
        IntStream.range(0,NUMB_OF_ELITE_SCHEDULES).forEach(x->crossoverPopulation.getSchedules()
                .set(x,population.getSchedules().get(x)));

        IntStream.range(NUMB_OF_ELITE_SCHEDULES,population.getSchedules().size()).forEach(x->{
            if(CROSSOVER_RATE > Math.random()){
                Individual schedule1 = selectTournamentPopulation(population).sortByFitness().getSchedules().get(0);
                Individual schedule2 = selectTournamentPopulation(population).sortByFitness().getSchedules().get(0);
                crossoverPopulation.getSchedules().set(x,crossoverSchedule(schedule1,schedule2));
            }else{
                crossoverPopulation.getSchedules().set(x,population.getSchedules().get(x));
            }
        });
        return crossoverPopulation;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    Individual crossoverSchedule(Individual schedule1, Individual schedule2){
        Individual crossoverSchedule = new Individual(myTask).initialize();
        IntStream.range(0,crossoverSchedule.getTask().size()).forEach(x->{
            if (Math.random()>0.5) crossoverSchedule.getTask().set(x, schedule1.getTask().get(x));
            else{
                crossoverSchedule.getTask().set(x, schedule2.getTask().get(x));
            }
        });
        return crossoverSchedule;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    Population mutatePopulation(Population population){
        Population mutatePopulation=new Population(population.getSchedules().size(),myTask);
        ArrayList<Individual> schedules =  mutatePopulation.getSchedules();
        IntStream.range(0,NUMB_OF_ELITE_SCHEDULES).forEach(x->schedules.set(x, population.getSchedules().get(x)));
        IntStream.range(NUMB_OF_ELITE_SCHEDULES, population.getSchedules().size()).forEach(x->{
            schedules.set(x, mutateSchedule(population.getSchedules().get(x)));
        });
        return  mutatePopulation;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    Individual mutateSchedule(Individual mutateSchedule){
        Individual schedule = new Individual(myTask).initialize();
        IntStream.range(0,mutateSchedule.getTask().size()).forEach(x->{
            if(MUTATION_RATE>Math.random())mutateSchedule.getTask().set(x, schedule.getTask().get(x));
        });
        return mutateSchedule;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    Population selectTournamentPopulation(Population population){
        Population tournamentPopulation = new Population(TOURNAMENT_SELECTION_SIZE,myTask);
        IntStream.range(0,TOURNAMENT_SELECTION_SIZE).forEach(x->{
            tournamentPopulation.getSchedules().set(x,population.getSchedules().get((int)(Math.random()*population.getSchedules().size())));
        });
        return tournamentPopulation;
    }
}
