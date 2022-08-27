package dev.bourg.level2bot.data.objects.states;

public class State {

   private final Integer peoplePresent;
   private final Boolean open;


    public State(Integer peoplePresent, Boolean open) {
        this.peoplePresent = peoplePresent;
        this.open = open;
    }

    public Boolean getOpen() {
        return open;
    }

    public Integer getPeoplePresent() {
        return peoplePresent;
    }
}
