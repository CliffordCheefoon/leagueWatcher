package models;

public class playerWeeklyValue {

    public String playerName;
    public String playerIcon;
    public int foundValue = 0;


    
    public playerWeeklyValue(String playerName, String playerIcon, int foundValue) {
        this.playerName = playerName;
        this.playerIcon = playerIcon;
        this.foundValue = foundValue;
    }



    @Override
    public String toString() {
        return "playerWeeklyValue [foundValue=" + foundValue + ", playerIcon=" + playerIcon + ", playerName="
                + playerName + "]";
    }


    
    

    

    
}
