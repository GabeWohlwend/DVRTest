import java.util.ArrayList;
import java.util.Scanner;

public class DVRTest
{
	public static Router[] routerArray;
	public static Scanner usrIn = new Scanner(System.in);
	public static boolean improperInput = false;
	public static int userSelection = 0;
	public static int networkSize = 0;
	public static int[][] delay;
	public static  String[] alphabet = {"A","B","C","D","E","F","G",
										"H","I","J","K","L","M","N",
										"O","P","Q","R","S","T","U",
										"V","W","X","Y","Z"};

	public static ArrayList<String> routerNames = new ArrayList<String>();
	
	public static void main(String[] args){

		//prompt for network size
		do{
			PromptForSize();
		}while(improperInput);
		
		//testing code
		//networkSize = 15;
		
		//populate routerNames array
		PopulateNameArray();
		
		//populate with user submitted data
		do{
			PopulateDelayMatrix();
			
			//testing array by populating automatically
			//TestPopulateDelayMatrix();
			
			
			ConfirmDelayMatrix();
			
		}while(improperInput);
		
		//make original router objects
		InitializeRouterArray();
		
		//print initial tables
		System.out.println("Initial State Of Routing Tables\n-------------------------------");
		PrintRoutingTables();
		
		
		System.out.println("Starting Router Threads\n-----------------------\n");
		StartThreads();
		
		
	}
	
	//method that starts threads
	public static void StartThreads(){
		for(int i = 0; i < networkSize; i++){
			routerArray[i].start();
		}
	}
	
	//method to initialize routers
	public static void InitializeRouterArray(){
		routerArray = new Router[networkSize];
		for(int i = 0; i < networkSize; i++){
			ArrayList<Integer> routerCost = new ArrayList<Integer>();
			ArrayList<String> nextRouter = new ArrayList<String>();
			for(int j = 0; j < networkSize; j++){
				routerCost.add(delay[i][j]);
				if(delay[i][j] != -1){
					nextRouter.add(alphabet[j]);
				}
				else{
					nextRouter.add("-");
				}
			}
			routerArray[i] = new Router(i, networkSize, routerNames, routerCost, nextRouter);
		}
	}
	
	//method to print out routing tables
	public static void PrintRoutingTables(){
		for(int i = 0; i < networkSize; i++){
			System.out.println("Routing Table for node " + alphabet[i] + ":");
			System.out.println(routerArray[i].TableToString());
		}
	}
	
	//method with logic to confirm matrix is correct
	private static void ConfirmDelayMatrix()
	{
		PrintDelayMatrix();
		boolean badInput = false;
		do{
			if(!badInput){
				System.out.print("\nIs this info correct? Enter \"1\" for YES, enter \"2\" for NO: ");
			}
			userSelection = usrIn.nextInt();
			if(userSelection == 1){
				improperInput = false;
				badInput = false;
			}
			else if(userSelection == 2){
				System.out.print("Please re-enter the delays\n--------------------------");
				improperInput = true;
				badInput = false;
			}
			else{
				System.out.print("Please select \"1\" or \"2\": ");
				badInput = true;
			}
		}while(badInput);
	}

	//method prompts user for size and stores result
	private static void PromptForSize()
	{
		System.out.print("How many routers are there in the topography? (up to 26): ");
		networkSize = usrIn.nextInt();
		if(networkSize > 26 || networkSize < 1){
			improperInput = true;
			System.out.println("Please enter a number between 1 and 26");
		}
		else{
			improperInput = false;
		}
	}

	//method populates routerNames array with letters according to network size
	private static void PopulateNameArray()
	{
		for(int i = 0; i < networkSize; i++){
			routerNames.add(alphabet[i]);
			routerNames.trimToSize();
		}
	}

	//method that returns the formatted string of the delay matrix
	public static String DelayMatrixToString(){
		String returnString = "Topology matrix:\n";
		
		returnString += "\t";
		for(int i = 0; i < networkSize; i++){
			returnString += "|" + routerNames.get(i).toString() + "|\t";
		}
		
		for(int i = 0; i < networkSize; i++){
			returnString += "\n|" +  routerNames.get(i).toString() + "|";
			for(int j = 0; j < networkSize; j++){
				returnString += "\t " + delay[i][j];
			}
		}
		return returnString;	
	}
	
	//method that prints the delay matrix
	public static void PrintDelayMatrix(){
		System.out.print(DelayMatrixToString());
	}
	
	//method for automatically populating delay matrix for testing/debugging
	public static void TestPopulateDelayMatrix(){
		delay = new int[networkSize][networkSize];
		for(int i = 0; i < networkSize; i++){
			for(int j = 0; j < networkSize; j++){
				delay[i][j] = 3;
			}
		}
	}
	
	//method for populating delay matrix
	public static void PopulateDelayMatrix(){
		delay = new int[networkSize][networkSize];
		System.out.println();
		for(int i = 0; i < networkSize; i++){
			for(int j = 0; j+i < networkSize; j++){
				if(i != j+i){
					System.out.print("Enter the delay from " + routerNames.get(i).toString() + " to " + 
										routerNames.get(j+i).toString() + ": (Enter -1 for infinity): ");
					int input = usrIn.nextInt();
					delay[i][j+i] = input;
					delay[j+i][i] = input;
				}
				else{
					delay[i][j+i] = -1;
				}
			}
		}
	}
}
