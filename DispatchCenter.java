import java.util.*;

public class DispatchCenter {
    public static String[] AREA_NAMES = {"Downtown", "Airport", "North", "South", "East", "West"};

    private int[][]  stats; // You'll need this for the last part of the assignment
    private HashMap<Integer,Taxi> taxis;
    private HashMap<String, ArrayList<Taxi>> areas;
    private ArrayList<Taxi> result;
    // Constructor
    public DispatchCenter() {
        // You'll need this for the last part of the assignment
        stats = new int[AREA_NAMES.length][AREA_NAMES.length];
        taxis = new HashMap<>(50);
        areas = new HashMap<>(6);
        for (int i = 0; i < 6; i++) {
            areas.put(AREA_NAMES[i], result);
        }
        for(int i = 0; i<50;i++) {
            int randomPlate = 100 + (int) (Math.random() * 900);
            String randomDest = AREA_NAMES[(int) (Math.random() * 6)];
            Taxi taxi = new Taxi(randomPlate);
            taxi.setDestination(randomDest);

            taxis.put(randomPlate, taxi);
            addTaxi(taxi, randomDest);
        }
    }

    // You'll need this for the last part of the assignment
    public int[][]   getStats() { return stats; }

    // Update the statistics for a taxi going from the pickup location to the dropoff location
    public void updateStats(String pickup, String dropOff) {
        int pickupIndex = -1; int dropoffIndex = -1;
        for(int i = 0;i<AREA_NAMES.length;i++){
            if(AREA_NAMES[i]==pickup){
                pickupIndex = i;
            }
            if(AREA_NAMES[i] == dropOff){
                dropoffIndex = i;
            }

//            stats[pickupIndex][dropoffIndex]+=1;
        }
        stats[pickupIndex][dropoffIndex]+=1;


        
    }
    
    // Determine the travel times from one area to another
    public static int computeTravelTimeFrom(String pickup, String dropOff) {
        Integer [][] travelTime = new Integer[6][6];
        travelTime[0][0] = 10;travelTime[0][1] = 40;travelTime[0][2] = 20;travelTime[0][3] = 20;travelTime[0][4] = 20;travelTime[0][5] = 20;
        travelTime[1][0] = 40;travelTime[1][1] = 10;travelTime[1][2] = 40;travelTime[1][3] = 40;travelTime[1][4] = 20;travelTime[1][5] = 60;
        travelTime[2][0] = 20;travelTime[2][1] = 40;travelTime[2][2] = 10;travelTime[2][3] = 40;travelTime[2][4] = 10;travelTime[2][5] = 20;
        travelTime[3][0] = 20;travelTime[3][1] = 40;travelTime[3][2] = 40;travelTime[3][3] = 10;travelTime[3][4] = 20;travelTime[3][5] = 20;
        travelTime[4][0] = 20;travelTime[4][1] = 20;travelTime[4][2] = 20;travelTime[4][3] = 20;travelTime[4][4] = 10;travelTime[4][5] = 40;
        travelTime[5][0] = 20;travelTime[5][1] = 60;travelTime[5][2] = 20;travelTime[5][3] = 20;travelTime[5][4] = 40;travelTime[5][5] = 10;
        int pickupIndex=-1; int dropoffIndex=-1;
        for(int i = 0;i<AREA_NAMES.length;i++){
            if(AREA_NAMES[i] == pickup){
                pickupIndex = i;
            }
            if(AREA_NAMES[i] == dropOff){
                dropoffIndex = i;
            }
        }
        return travelTime[pickupIndex][dropoffIndex];
//        return 0;
    }

    // Add a taxi to the hashmaps
    public void addTaxi(Taxi aTaxi, String area) {
        if (area == aTaxi.getDestination()) {
            if (areas.get(area) != null) {
                areas.get(area).add(aTaxi);
//                taxis.put(aTaxi.getPlateNumber(), aTaxi);
            } else {
                result = new ArrayList<>();
                result.add(aTaxi);
                areas.put(area, result);
            }
            taxis.put(aTaxi.getPlateNumber(), aTaxi);

        }
    }

    // Return a list of all available taxis within a certain area
    private ArrayList<Taxi> availableTaxisInArea(String s) {
        ArrayList<Taxi> result = new ArrayList<Taxi>();
        // check each of taxi if available int current area and add it to result list
        for (Taxi t:areas.get(s)){
            if(t.getAvailable()){
                result.add(t);
            }
        }
        return result;

    }

    // Return a list of all busy taxis
    public ArrayList<Taxi> getBusyTaxis() {
        ArrayList<Taxi> result = new ArrayList<Taxi>();
        for (int i = 0; i<AREA_NAMES.length;i++){
            for (Taxi t:areas.get(AREA_NAMES[i])){
                if(!t.getAvailable()){
                    result.add(t);
                }
            }
        }
        return result;
    }

    // Find a taxi to satisfy the given request
    public Taxi sendTaxiForRequest(ClientRequest request) {
        String pickupLoc = request.getPickupLocation();
        String dropoffLoc = request.getDropoffLocation();
       if(availableTaxisInArea(pickupLoc).size()!=0){
           Taxi sendTaxi = availableTaxisInArea(pickupLoc).get(0);
           areas.get(pickupLoc).remove(0);
           areas.get(dropoffLoc).add(sendTaxi);
           areas.get(dropoffLoc).get(areas.get(dropoffLoc).size()-1).setAvailable(false);
           sendTaxi.setEstimatedTimeToDest(computeTravelTimeFrom(pickupLoc,dropoffLoc));
           updateStats(dropoffLoc,pickupLoc);
           return sendTaxi;

       }else if(availableTaxisInArea(pickupLoc).size() == 0){
           for(int i = 0;i<AREA_NAMES.length;i++){
               if(areas.get(AREA_NAMES[i]).get(0).getAvailable()){
                   Taxi sendTaxi = availableTaxisInArea(AREA_NAMES[i]).get(0);
                   areas.get(AREA_NAMES[i]).remove(0);
                   areas.get(dropoffLoc).add(sendTaxi);
                   areas.get(dropoffLoc).get(areas.get(dropoffLoc).size()-1).setAvailable(false);
                   sendTaxi.setEstimatedTimeToDest(computeTravelTimeFrom(AREA_NAMES[i],pickupLoc)+computeTravelTimeFrom(pickupLoc,dropoffLoc));
                   updateStats(dropoffLoc,pickupLoc);
                   return sendTaxi;
               }
           }
       }else if(getBusyTaxis().size() == 0){
           return null;
       }
        return null;
    }

    public HashMap<String, ArrayList<Taxi>> getAreas() {
        return areas;
    }
}