

	   import java.awt.image.BufferedImage;
	   import java.io.File;
	   import java.io.FileWriter;
	   import java.io.IOException;
	   import javax.imageio.ImageIO;
	   import java.util.Random;
	   import swiftbot.*;

	   public class swiftbot {

	   	private static final String IMAGE_FILE_NAME = "savedimage.jpg";
	       private static SwiftBotAPI swiftbot = new SwiftBotAPI();
	       private static int greentimes = 0;
	       private static double OverallHighestaverageIntensityLeft= Double.MAX_VALUE;
	       private  static double  OverallHighestaverageIntensityCentre= Double.MAX_VALUE;
	       private  static double  OverallHighestaverageIntensityRight= Double.MAX_VALUE;
	       private static double totalDistance;
	       private static double averageIntensityLeft;
	       private static double averageIntensityCentre;
	       private static double averageIntensityRight;
	       private static long StartTimer;
	       private static long EndTime;
	       private static boolean objectDetected = false;
	   	private static double totaldistanceTravelled=0;



	   	 public static void main(String[] args) {
	   		 
	   	        try {
	   	            start();
	   	            Thread.sleep(1500);
	   	            searchForLight();
	   	            lightsourcefound(averageIntensityLeft, averageIntensityCentre, averageIntensityRight);
	   	        } catch (Exception e) {
	   	            e.printStackTrace();
	   	        }
	   	    }
// used to track the time where programme execution start
	   public static void StartTimer() {
	   	StartTimer = System.currentTimeMillis();
	   }
	   
	   public static void StopTimer() {
	   	EndTime= System.currentTimeMillis();
	   }
	   
	   public static long Duration() {
	   	return EndTime-StartTimer;
	   }

	   //distance
	   public static double calculateDistance(double startX, double startY, double endX,double endY) {
			
			double distanceX= endX-startX;
			double distanceY= endY-startY;
			return Math.sqrt(distanceX*distanceX +distanceY * distanceY);

		}
		//updated distance travelled
		public static void updateDistance(double startX, double startY, double endX,double endY) {
			double distance = calculateDistance(startX,startY,endX,endY);
			totaldistanceTravelled+=distance;
		}
		//overall higher intensities
		
		 static double getOverallHighestaverageIntensityLeft() {
			return OverallHighestaverageIntensityLeft;
		}
		 static double getOverallHighestaverageIntensityCentre() {
		return OverallHighestaverageIntensityCentre;
		}
		 static double getOverallHighestaverageIntensityRight() {
	       return OverallHighestaverageIntensityRight;
		}
		
		//method run first including methods for enabling the buttons
	   	public static void start() throws InterruptedException {
	   		inter1(); 
	   		StartTimer();
	   		try {
	   			Thread.sleep(2000);
	   			buttonA();
	   			buttonX();
	   			Thread.sleep(1100);
	   		} catch (InterruptedException e) {
	   			e.printStackTrace();
	   		} catch (IllegalArgumentException e) {
	   			e.printStackTrace();
	   			// Handle IllegalArgumentException here
	   		}

	   		// Error handling when wrong button pressed
	   		swiftbot.enableButton(Button.Y, () -> {
	   			inter1error();
	   			swiftbot.disableButton(Button.Y);
	   		});
	   		swiftbot.enableButton(Button.B, () -> {
	   			swiftbot.disableButton(Button.B);
	   			inter1error();
	   		});

	   	}



	   	///welcome for the first interface on command palette
	   	static void inter1() {

	   		System.out.println(" ****************************************************");
	   		System.out.println("  Welcome to search for light programme");
	   		System.out.println(" ****************************************************");
	   		System.out.println();
	   		System.out.println("Press button‘A’to start the programme");
	   		System.out.println("Press  button‘X'to terminate the programme");
	   		System.out.println();
	   		System.out.println("Thank you for your input the Swiftbot will start the execution now");
	   	}
	   
	   	// when button A has been pressed
	   	public static void buttonA() {
	   		swiftbot.enableButton(Button.A, () -> {
	   			System.out.println("Button A has been pressed");
	   			swiftbot.disableButton(Button.A);
	   			swiftbot.disableButton(Button.X);
	   			try {
	   				searchForLight();
	   			} catch (Exception e) {

	   				e.printStackTrace();
	   			}

	   		});
	   	}
	   	//main method where light search and detecting objects take place
	   	 public static void searchForLight() {
	   	        while (!objectDetected) {
	   	            try {
	   	                BufferedImage img = camera(); // Capture image
	   	                if (img != null) {
	   	                    // Image captured successfully
	   	                    File outfile = new File(IMAGE_FILE_NAME);
	   	                    ImageIO.write(img, "jpg", outfile);
	   	                    underlightgreen();
	   	                    greentimes++;
	   	                    divideandaveragecolumns(img);
	   	                    detectObj(); // Check for object after image capture
	   	                    Thread.sleep(10000); // Wait for 10 seconds before capturing next image
	   	                } else {
	   	                    // Image capture failed
	   	                    System.out.println("The uploaded image does not meet the minimum quality requirements");
	   	                }
	   	            } catch (Exception e) {
	   	                // Exception occurred during image capture or processing
	   	                System.out.println("Error occurred when processing image!");
	   	                e.printStackTrace();
	   	            }
	   	        }
	   	    }
	   	 // when image fails to meet the requirement of 480 pixels
	   	public static BufferedImage camera() throws InterruptedException, IOException {
	   	    BufferedImage img = null;
	   	    try {
	   	        img = swiftbot.takeGrayscaleStill(ImageSize.SQUARE_480x480);
	   	    } catch (Exception e) {
	   	        System.out.println("Error occurred when taking image!");
	   	        e.printStackTrace();
	   	    }
	   	    return img;
	   	}



	   	//dividing the gray scale into 3 columns 
	   	public static String divideandaveragecolumns(BufferedImage img) {
	   		// Initialise variables to store the sum of RGB values for each column
	   		int SumLeftColumn =0;
	   		int SumCentreColumn=0;
	   		int SumRightColumn=0;
	   	// Get the width and height of the image
	   		int w=img.getWidth();
	   		int h= img.getHeight();
	   	// Loop through each pixel in the image
	   		for (int y = 0; y < h; y++) {
	   			for (int x = 0; x < w; x++) {
	   			// Get the RGB value of the pixel
	   				int p = img.getRGB(x, y);
	   			// Extract the red, green, and blue components from the RGB value
	   				int r = (p >> 16) & 0xFF;
	   				int g = (p >> 8) & 0xFF;
	   				int b = p & 0xFF;
	   			  // Determine which column the pixel belongs to and accumulate the RGB values accordingly
	   				if (x<w/3) {
	   					SumLeftColumn+=(r+g+b);	
	   				}else if(x<(2*w/3)) {
	   					SumCentreColumn+=(r+g+b);
	   				}else {
	   					SumRightColumn+=(r+g+b);
	   				}
	   			}
	   		}
	   	    // Calculate the average intensity for each column
	   		double averageIntensityLeft = (double) SumLeftColumn / (w * h / 3);
	   		double averageIntensityCentre = (double) SumCentreColumn / (w * h / 3);
	   		double averageIntensityRight = (double) SumRightColumn / (w * h / 3);
	   	    // Calculate the average intensity for each column
	   		System.out.println("The average light intensity of Left column is: " + averageIntensityLeft);
	   		System.out.println("The average light intensity of Centre column is: " + averageIntensityCentre);
	   		System.out.println("The average light intensity of Right column is: " + averageIntensityRight);
	   		 

	   	 // Determine which column has the highest average intensity and move the swiftbot accordingly

	   		try {
	   			if (averageIntensityLeft > averageIntensityCentre && averageIntensityLeft > averageIntensityRight) {
	   			    System.out.println("The average light intensity of the left column is the highest");
	   			    swiftbot.move(0, 100, 450); // Move left
	   			} else if (averageIntensityCentre > averageIntensityLeft && averageIntensityCentre > averageIntensityRight) {
	   			    System.out.println("The average light intensity of the centre column is the highest");
	   			    swiftbot.move(100, 100, 450); // Move centre
	   			} else if (averageIntensityRight > averageIntensityCentre && averageIntensityRight > averageIntensityLeft) {
	   			    System.out.println("The average light intensity of the right column is the highest");
	   			    swiftbot.move(100, 0, 450); // Move right
	   			}else {
	   				
	   			}

	   		} catch (IllegalArgumentException e) {
	   			e.printStackTrace();
	   		}
	   		return null;
	   	}



	   	//is an object detected
	   	public static void detectObj() {
	           try {
	               double distanceToObjectstill = swiftbot.useUltrasound();
	               System.out.println("Distance to object: " + distanceToObjectstill + " cm.");
	               if (distanceToObjectstill <= 50) {
	                   System.out.println("Object detected, please remove");
	                   underlightred();
	                   Thread.sleep(500);
	                   swiftbot.disableUnderlights();
	                   objectDetected = true;
	               }
	           } catch (InterruptedException e) {
	               e.printStackTrace();
	           }
	       }






	   	// has object been removed
	   	public static void  hasobjectbeenremoved() throws InterruptedException, IOException{
	   		double distanceToObjectstill =swiftbot.useUltrasound();
	   		if (distanceToObjectstill <= 50) {
	   			System.out.println("Object detected!Programme ended");
	   			try {
	   				underlightred();
	   				// Log file into text file??
	   				convertlogtotext();
	   				// Display log information
	   				displaylog();
	   			}catch (Exception e) {
	   				e.printStackTrace();
	   			}
	   			System.exit(0); 

	   		}else {
	   			System.out.println("Object removed");
	   			searchForLight();
	   		}
	   	}

	   	public static void lightsourcefound(double averageIntensitycolumnLeft, double averageIntensitycolumnCentre, double averageIntensitycolumnRight)
	   			throws IOException, InterruptedException {
//if light source is not found when all the columns have the same intensities
	   		if (averageIntensitycolumnLeft == averageIntensitycolumnCentre && 
	   				averageIntensitycolumnCentre == averageIntensitycolumnRight) {
	   			System.out.println("Light source not detected!");
	   			// Stop for 0.5s
	   			try {
	   				swiftbot.move(0, 0, 700);
	   			} catch (IllegalArgumentException e) {
	   				e.printStackTrace();
	   			}
	   			// Move randomly right or left 90 degrees

	   		}randomnumber();
	   		try {
	   			searchForLight();
	   		} finally {
	   			displaylog();
	   		}
	   	}

	   	// randomly select direction of movement
	   	static void randomnumber() throws InterruptedException {
	   	// Create a Random object to generate random numbers
	   		Random random = new Random();
	   	 // Generate a random number between 0 (inclusive) and 2 (exclusive)
	   		int direction = random.nextInt(2);
	   	 // Check the random number to determine the direction of movement
	   		if (direction == 0) {
	   			//if direction is 0, move left
	   			try {
	   				swiftbot.move(0,70,1000);
	   				Thread.sleep(1100);
	   			} catch (IllegalArgumentException e) {
	   				e.printStackTrace();
	   			}
	   			System.out.println("Swiftbot rotated 90 degrees to the left.");
	   		} else {
	   			try {
	   				swiftbot.move(70, 0, 1000);
	   				Thread.sleep(1100); 
	   			} catch (IllegalArgumentException e) {
	   				e.printStackTrace();
	   			}

	   			System.out.println("Swiftbot rotated 90 degrees to the right.");
	   		}
	   	}

	   	static void convertlogtotext() {
	   		try {
	   			// Create or append to the log file
	   			FileWriter logWriter = new FileWriter("searchforlightlog.txt", true);
	   			logWriter.write("Log information:\n");
	   			logWriter.write("Overall highest average intensity observed in left section: " +OverallHighestaverageIntensityLeft + "\n");
	   			logWriter.write("Overall highest average intensity observed in centre section: " + OverallHighestaverageIntensityCentre + "\n");
	   			logWriter.write("Overall highest average intensity observed in right section: " + OverallHighestaverageIntensityRight + "\n");
	   			logWriter.write("Number of times the Swiftbot detected light: " + greentimes + "\n");
	   			logWriter.write("Total distance travelled: " + totaldistanceTravelled/100.0 + "metres");
	   			logWriter.write("Duration of the execution: " + Duration() + " milliseconds");
	   			logWriter.close();
	   			System.out.println("Log information has been written to the log file.");
	   		} catch (IOException e) {
	   			System.out.println("An error occurred while writing to the log file.");
	   			e.printStackTrace();
	   		}
	   	}

	   	public static void displaylog() {
	   		try {
	   			interfaceloginformation();
	   			Thread.sleep(1000);

	   			// Disable any previously attached functions before adding new ones
	   			swiftbot.disableButton(Button.X);
	   			swiftbot.disableButton(Button.Y);
	   			swiftbot.disableButton(Button.A);
	   			swiftbot.disableButton(Button.B);

	   			swiftbot.enableButton(Button.X, () -> {
	   				System.out.println("Button X has been pressed");
	   				System.exit(0);
	   			});

	   			swiftbot.enableButton(Button.Y, () -> {
	   				System.out.println("Button Y has been pressed");
	   				StopTimer();
	   				interfacebuttonypressed(averageIntensityLeft, averageIntensityCentre, averageIntensityRight, greentimes, totalDistance, Duration());
	   				convertlogtotext();
	   				try {
	   					Thread.sleep(1100);
	   				} catch (InterruptedException e) {
	   					e.printStackTrace();
	   				}
	   				
	   				System.exit(0);
	   			});

	   			swiftbot.enableButton(Button.A, () -> {
	   				interfacelogininformationerror();
	   			});

	   			swiftbot.enableButton(Button.B, () -> {
	   				interfacelogininformationerror();
	   			});

	   		} catch (Exception e) {
	   			System.out.println("An error occurred while displaying the log.");
	   			e.printStackTrace();
	   		}
	   	}

	   	static void interfaceloginformation() {
	   		System.out.println(" ****************************************************");
	   		System.out.println("Would you like to display the log of information ?");
	   		System.out.println(" ****************************************************");
	   		System.out.println(" Press the‘X’button for NO \r\n"
	   				+ " Press the‘Y’button for YES");
	   		System.out.println(" Your chosen option will result in the conversion of login formation to a text file.");
	   		System.out.println(" Thank you for using the Search for Light programme!");
	   	}
	   	static void interfacelogininformationerror () {
	   		System.out.println(" ****************************************************");
	   		System.out.println("Would you like to display the log of information ?");
	   		System.out.println(" ****************************************************");
	   		System.out.println(" Press the‘X’button for NO \r\n"
	   				+ " Press the‘Y’button for YES");
	   		System.out.println(" Invalid input! Please enter one of the following: ");
	   		System.out.println(" Press the‘X’button for NO \r\n"
	   				+ " Press the‘Y’button for YES");
	   		System.out.println(" Your chosen option will result in the conversion of login formation to a text file.");
	   		System.out.println(" Thank you for using the Search for Light programme!");
	   	}

	   	static void interfacebuttonypressed(double averageIntensityLeft2, double averageintensityCentre2, double averageIntensityRight2, int greentimes2, double totalDistance2, long duration2) {
	   		System.out.println(" ****************************************************");
	   		System.out.println(" Would you like to display the log of information ?");
	   		System.out.println(" ****************************************************");
	   		System.out.println(" Press the‘X’button for NO \r\n"
	   				+ " Press the‘Y’button for YES");
	   		System.out.println(" Overall  highest average intensity observed in left section: "+ OverallHighestaverageIntensityLeft);
	   		System.out.println(" Overall highest average intensity observed in centre section: "+ OverallHighestaverageIntensityCentre);
	   		System.out.println(" Overall highest average intensity observed in right section:  "+OverallHighestaverageIntensityRight );
	   		System.out.println(" Number of times the Swiftbot detected light: " + greentimes); 
	   		System.out.println(" Movements of the Swiftbot: ");
	   		//converts the total distance travelled from centimetres to metres
	   		double totaldistanceTravelledmetres = totaldistanceTravelled/100.0;
	   		System.out.println(" Total distance travelled: "+ totaldistanceTravelledmetres + " metres"); 
	   		System.out.println(" Duration of the execution: " + Duration() + " millisiseconds"); 
	   		System.out.println(" Your chosen option will result in the conversion of login formation to a text file.");
	   		System.out.println(" Thank you for using the Search for Light programme!"); 
	   	}



	   	//set under lights to green 
	   	static void underlightgreen() throws IOException, InterruptedException { 
	   		int[] colourToLightUp = {0, 0, 255}; 
	   		try {
	   			swiftbot.fillUnderlights(colourToLightUp);
	   			Thread.sleep(1000);
	   		} catch (InterruptedException e) {
	   			e.printStackTrace();
	   		}
	   		swiftbot.disableUnderlights();

	   	} 
	   	static void underlightred() { 
	   		int[] colourToLightUp = {255, 0, 0}; 
	   		try {
	   			swiftbot.fillUnderlights(colourToLightUp);
	   			Thread.sleep(1000);
	   		} catch (InterruptedException e) {
	   			e.printStackTrace();
	   		}
	   		swiftbot.disableUnderlights();

	   	}


	   	//press buttoned X
	   	public static void buttonX() {
	   		swiftbot.enableButton(Button.X, () -> {
	   			System.out.println("Button X has been pressed");
	   			swiftbot.disableButton(Button.X);
	   			System.exit(0);
	   		});
	   	}

	   	//what interface shows when invalid button pressed
	   	static void inter1error() {
	   		System.out.println(" ****************************************************");
	   		System.out.println("  Welcome to search for light programme");
	   		System.out.println(" ****************************************************");
	   		System.out.println();
	   		System.out.println("Press button‘A’to start the programme");
	   		System.out.println("Press  button‘X'to terminate the programme");
	   		System.out.println();
	   		System.out.println("Invalid input!Please press one of the following: ");
	   		System.out.println();
	   		System.out.println("Press button‘A’to start the programme");
	   		System.out.println("Press  button‘X'to terminate the programme");
	   		System.out.println();
	   		System.out.println("Thank you for your input the Swiftbot will start the execution now");
	   	}  

	   }



