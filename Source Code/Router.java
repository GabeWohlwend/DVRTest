import java.util.ArrayList;

public class Router implements Runnable{

	public Thread t;
	public int routerNumber;
	public ArrayList<String> routerNames = new ArrayList<String>();
	public ArrayList<Router> neighborRouters = new ArrayList<Router>();
	public ArrayList<Integer> neighbors = new ArrayList<Integer>();
	public ArrayList<Integer> routerCost = new ArrayList<Integer>();
	public ArrayList<String> nextRouter = new ArrayList<String>();
	public int networkSize;
	
	public Router(int num, int networkSize, ArrayList<String> tempRouterNames, 
				  ArrayList<Integer> tempRouterCost, ArrayList<String> tempNextRouter){
		this.routerNumber = num;
		this.networkSize = networkSize;
		this.routerNames = new ArrayList<>(tempRouterNames);
		this.routerCost =  new ArrayList<>(tempRouterCost);
		this.nextRouter =  new ArrayList<>(tempNextRouter);
		
	}
	
	//default constructor for initialization
	public Router(){
		
	}

	@Override
	public void run()
	{
		
		//get neighboring tables
		for(int i = 0; i < networkSize; i++){
			if(routerCost.get(i) != -1){
				neighbors.add(i);
			}
		}
		
		//loop over and over every two seconds
		for(int i = 0; i < networkSize; i++){
			//clear then re-add router info
			neighborRouters.clear();
			for(int j = 0; j < neighbors.size(); j++){
				neighborRouters.add(DVRTest.routerArray[neighbors.get(j)].GiveCopy());
			}
			
			//wait a sec
			try
			{
				Thread.sleep(1000);
			}catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			//alter tables
			for(int j = 0; j < neighbors.size(); j++){
				ReceiveAndUpdateTable(neighborRouters.get(j));
			}
			
			//wait a sec
			try
			{
				Thread.sleep(1000);
			}catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			//print only on one thread
			if(routerNumber == 0){
				if(i == (networkSize-1)){
					System.out.println("Tables for last round number " + (i+1) + "\n-----------------------------");
					DVRTest.PrintRoutingTables();
				}
				else{
					System.out.println("Tables for round number " + (i+1) + "\n-------------------------");
					DVRTest.PrintRoutingTables();
				}
			}
		}
	}

	public void start () {
		if (t == null) {
			t = new Thread (this);
	        t.start ();
	    }
	}
	
	public String TableToString(){
		String returnString = "";
		
		returnString += "Dest\tDelay\tOut\n";
		for(int i = 0; i < networkSize; i++){
			returnString += routerNames.get(i).toString() + "\t" +
						    routerCost.get(i).toString() + "\t" +
						    nextRouter.get(i).toString() + "\n";

		}
		return returnString;
	}
		
	public void ReceiveAndUpdateTable(Router neighbor){
		//System.out.println(DVRTest.alphabet[routerNumber] + " got info for " + DVRTest.alphabet[neighbor.routerNumber]);
	
		
		for(int i = 0; i < networkSize; i++){
			
			if(i != routerNumber && neighbor.routerCost.get(i) != -1){
				if(routerCost.get(i) == -1){
					routerCost.set(i, neighbor.routerCost.get(i) + routerCost.get(neighbor.routerNumber));
					nextRouter.set(i, DVRTest.alphabet[neighbor.routerNumber]);
				}
				else if((neighbor.routerCost.get(i) + routerCost.get(neighbor.routerNumber)) < routerCost.get(i)){
					routerCost.set(i, neighbor.routerCost.get(i) + routerCost.get(neighbor.routerNumber));
					nextRouter.set(i, DVRTest.alphabet[neighbor.routerNumber]);
				}
			}
			
		}		
	}
	
	public synchronized Router GiveCopy(){
		Router tempRouter = new Router(routerNumber, networkSize, routerNames, routerCost, nextRouter);
		notifyAll();
		return tempRouter;
	}
}
