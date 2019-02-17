package com.kodilla.stream.world;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final  class World {

    private final List<Continet> continents = new ArrayList<>();

    public void addContinent(Continet continet){
        continents.add(continet);
    }

    public List<Continet> getContinents() { return continents; }

    public List<Continet> getPreparedData(){

        Continet europe = new Continet();
        Continet asia = new Continet();
        Country poland = new Country();
        Country russia = new Country();

        europe.addCountry(poland);
        asia.addCountry(russia);
        continents.add(europe);
        continents.add(asia);

        return continents;
    }


    public BigDecimal getPeopleQuantity(){

       BigDecimal totalPeopleQuantity = getPreparedData().stream()
               .flatMap(l -> l.getCountries().stream())
               .map(Country::getPeopleQuantity)
               .reduce(BigDecimal.ZERO, (sum, current) -> sum = sum.add(current));

       return totalPeopleQuantity;
    }
}
