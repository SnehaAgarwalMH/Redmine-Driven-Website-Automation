package com.AutoExcelProject.OpenTest;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.OutputType;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.NoSuchElementException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import org.openqa.selenium.interactions.Actions;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.openqa.selenium.JavascriptExecutor;
import io.netty.handler.timeout.TimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.monte.media.Format;
import org.monte.media.VideoFormatKeys;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import java.io.FileWriter;
import java.util.Calendar;
import java.io.PrintWriter;

public class ExcelWallet {
					
		private static WebDriver driver;
		private static WebDriverWait wait;
		private static RestTemplate restTemplate = new RestTemplate();
		private static HttpHeaders headers = new HttpHeaders();
		private static ScreenRecorder screenRecorder;		
		
		public static void main(String[] args) {

			System.setProperty("webdriver.chrome.driver", "My_Path\\chromedriver.exe");		
			
			// Start the scheduled tasks after login
			startScheduledTasks();
	        
	        //  wait to ensure that tasks have time to execute
	        try {
	            Thread.sleep(8 * 60 * 60 * 1000); // Sleep for 8 hours
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }		        	        			
		}
		
		private static void setup() throws Exception {
			
		      // Configure the video recording using MonteMedia
		      File videoFile = new File("C:\\Users\\sneha\\Documents\\Account 과\\Excelvideo\\recorded_video.mp4"); // Replace with the desired path for the video file
		      GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		      Rectangle captureSize = new Rectangle(
		    	        Toolkit.getDefaultToolkit().getScreenSize()); // Capture the entire screen
		      screenRecorder = new ScreenRecorder(gc, captureSize,
		              new Format(VideoFormatKeys.MediaTypeKey, VideoFormatKeys.MediaType.FILE, VideoFormatKeys.MimeTypeKey, VideoFormatKeys.MIME_AVI),
		              new Format(VideoFormatKeys.MediaTypeKey, VideoFormatKeys.MediaType.VIDEO, VideoFormatKeys.EncodingKey, VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
		                      VideoFormatKeys.CompressorNameKey, VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
		                      VideoFormatKeys.DepthKey, 24, VideoFormatKeys.FrameRateKey, Rational.valueOf(15),
		                      VideoFormatKeys.QualityKey, 1.0f, VideoFormatKeys.KeyFrameIntervalKey, 15 * 60),
		              new Format(VideoFormatKeys.MediaTypeKey, VideoFormatKeys.MediaType.VIDEO, VideoFormatKeys.EncodingKey, "black",
		                      VideoFormatKeys.FrameRateKey, Rational.valueOf(30)),
		              null , videoFile);	        		        
		}
		
		private static void StartVideoRecording() throws IOException {
						
		    	screenRecorder.start();		
	        
	    }
		
		private static void StopMonteScreenRecorder() throws IOException {
	        // Stop the video recording
	        screenRecorder.stop();
	    }
		
		private static void startScheduledTasks() {
			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
			Calendar targetTime = getTargetTime(10,54);
			long initialDelay = targetTime.getTimeInMillis() - System.currentTimeMillis();

			scheduler.schedule(() -> {
	            try {	            	
	            	ExecuteStart();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }, initialDelay, TimeUnit.MILLISECONDS);
		}
		  	   
		// Helper method to calculate the initial delay until the target time
		private static Calendar getTargetTime(int hourOfDay, int minute) {
			
			Calendar targetTime = Calendar.getInstance();
			targetTime.set(Calendar.HOUR_OF_DAY, 10);			        
			targetTime.set(Calendar.MINUTE, 54);
			targetTime.set(Calendar.SECOND, 0);
			targetTime.set(Calendar.MILLISECOND, 0);
			
			// If the current time is already past the target time, schedule for the next day
			if (System.currentTimeMillis() > targetTime.getTimeInMillis()) {
				targetTime.add(Calendar.DAY_OF_MONTH, 1);
			}

			return targetTime;
		}	    	  
		
		private static void ExecuteStart() throws IOException {
			
			ChromeOptions options = new ChromeOptions();		
			driver = new ChromeDriver(options);
			driver.manage().window().maximize();
			wait = new WebDriverWait(driver, Duration.ofSeconds(20));
							        
			//Set up Redmine API endpoint and headers
			String redmineApiEndPoint = "   ";
			String apiKey = "   ";
			HttpHeaders headers = new HttpHeaders();
			headers.set("X-Redmine-API-Key", apiKey);	
			headers.set("Accept", "application/json"); // Add this line to set the content type
						
			// Make an API request to get issue data
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.exchange(redmineApiEndPoint, HttpMethod.GET, new HttpEntity<>(headers), String.class);
			String responseBody = response.getBody();
						
			// Save the response to a text file
			try (PrintWriter writer = new PrintWriter(new FileWriter("response.txt"))) {
				writer.print(responseBody);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				setup();		        	
				//StartVideoRecording();			        
			 } catch (Exception e) {
			        e.printStackTrace();
			 }		
			
			// Process the issue data and perform actions
			processIssueDataAndPerformActions(responseBody, redmineApiEndPoint, apiKey);
					
			// Quit the driver after processing is complete
			driver.quit();
		}
					
		private static void processIssueDataAndPerformActions(String responseBody, String redmineApiEndPoint, String apiKey) {
			    						
			try {
				JSONObject responseObj = new JSONObject(responseBody);	
					        
				int totalCount = responseObj.getInt("total_count");
				int limit = responseObj.getInt("limit");
				int totalPages = (int) Math.ceil((double) totalCount / limit);
						            
				if (totalPages != 0) {

					for (int page = 1; page <= totalPages; page++) {
					
						String pageApiEndpoint = redmineApiEndPoint + "?page=" + page + "&query_id=7";
							            		
						HttpHeaders headers = new HttpHeaders();
						headers.set("X-Redmine-API-Key", apiKey);	
						headers.set("Accept", "application/json"); // Add this line to set the content type
							            		
						RestTemplate restTemplate = new RestTemplate();
						ResponseEntity<String> response = restTemplate.exchange(pageApiEndpoint, HttpMethod.GET, new HttpEntity<>(headers), String.class);
						String pageResponseBody = response.getBody();	            		
					
						processPageData(pageResponseBody);
					}
				} else {
					
					System.out.println("No pagination information found.\n\n\n\n\n\n");
					
			        // Process data without pagination
			        processPageData(responseBody);
				}								
				
			} catch (JSONException e) {
			        e.printStackTrace();
			}
		}

		private static void processPageData(String responseBody) {	
			
			try {
				
		        JSONObject responseObj = new JSONObject(responseBody);
		        JSONArray issuesArray = responseObj.getJSONArray("issues");	
		        

		        for (int i = 0; i < issuesArray.length(); i++) {
		        	
		        	 try {
		        		 StartVideoRecording();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
		        	
		            JSONObject issueObject = issuesArray.getJSONObject(i);
		            
		            // Extract custom fields array
		            JSONArray customFields = issueObject.getJSONArray("custom_fields");

		            String type = "";
		            String network = "";
		            String address = "";
		            String websiteLink = "";
		            String wallet = "";		            

		            // Loop through the custom fields to find the required values
		            for (int j = 0; j < customFields.length(); j++) {
		            	
		                JSONObject customField = customFields.getJSONObject(j);
		                String fieldName = customField.getString("name");
		                String fieldValue = customField.getString("value");
		                
		                if ("Type".equals(fieldName)) {
		                    type = fieldValue;
		                } else if ("Wallet".equals(fieldName)) {
		                    wallet = fieldValue;
		                } else if ("Network".equals(fieldName)) {
		                    network = fieldValue;
		                } else if ("Address".equals(fieldName)) {
		                    address = fieldValue;
		                } else if ("Website_Link".equals(fieldName)) {
		                    websiteLink = fieldValue;
		                }
		            }
		            if (websiteLink == null || websiteLink.trim().isEmpty()) {
		            	
		                System.out.println("No website link found for this issue. Skipping.\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		                
		                // Skip processing if there is no website link
		                continue;
		            }

		            String combinedMethodName = network + "_" + type + "_" +  wallet;

		            // Navigate to the website and perform actions
		            driver.get(websiteLink);
		            try {
			    		Thread.sleep(2000); // wait for 2 seconds
			        } catch (InterruptedException e) {
			            e.printStackTrace();
			        }

		            // Continue with existing website interaction methods
		            performAction(driver, address, combinedMethodName, network);	            

		            // Close the current website
		            driver.quit();

		            // Reinitialize the WebDriver to open a new website for the next issue
		            ChromeOptions options = new ChromeOptions();
		            driver = new ChromeDriver(options);
		            driver.manage().window().maximize();
		            wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		            
		          try {
						StopMonteScreenRecorder();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
		        }
		        		        		        
		    } catch (JSONException e) {
		        // Handle the case when JSON parsing fails
		        e.printStackTrace();
		    }
			
		}

		private static void performAction(WebDriver driver, String address, String combinedMethodName, String network) {
			
		    try {
		    	
		        // Invoke the website-specific method to perform the action
		        Method method = ExcelWallet.class.getDeclaredMethod(network, WebDriver.class, String.class, String.class);
		        method.invoke(null, driver, address, combinedMethodName);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}  		
			
		private static void BCH(WebDriver driver, String address, String combinedMethodName) throws IOException, InterruptedException {
			
			int i = 1;
			String Page = String.format("%02d", i);
			
			// Check if the security check page is present
			boolean isSecurityCheckPage = driver.getPageSource().contains("Checking if the site connection is secure");

			if (isSecurityCheckPage) {
				
				// Handle the security check page
				handleSecurityCheck();

				try {
					Thread.sleep(2000); // Wait for 2 seconds
				} catch (InterruptedException e) {
					e.printStackTrace();
				}		    		   
			}
	    	
			WebElement search = driver.findElement(By.cssSelector(".Input_input__13c07.SearchForm_input__1miNw.text-l.Input_addon-before-padding__2ZR1E.undefined.Input_font-size-sm__uGhyL"));
			search.click();
			try {
				Thread.sleep(2000); // wait for 2 seconds
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				    	
			search.clear(); // Clear any existing text in the search box
			search.sendKeys(address);      
			search.sendKeys(Keys.ENTER);
			try {
				Thread.sleep(5000); // wait for 2 seconds
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// Check if the security check page is present
			isSecurityCheckPage = driver.getPageSource().contains("Checking if the site connection is secure");

			if (isSecurityCheckPage) {
						
				// Handle the security check page
				handleSecurityCheck();

				try {
					Thread.sleep(2000); // Wait for 2 seconds
				} catch (InterruptedException e) {
					e.printStackTrace();
				}		    		   
			}
				        
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			String datetime = dateFormat.format(date);  	        
					        		       
			// Wait for a moment to load the content
			Thread.sleep(4000);  
		
		/*	 Robot robot = null;
			try {
				robot = new Robot();
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Capture the entire screen
		        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		        BufferedImage screenCapture = robot.createScreenCapture(screenRect);*/
		        
		     // Take a screenshot of the current visible part of the page
				File screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
				BufferedImage fullScreen = ImageIO.read(screenshotFile);
		        
		/*		// Combine the two screenshots
				BufferedImage combinedCapture = new BufferedImage(screenCapture.getWidth(), screenCapture.getHeight() + fullScreen.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D graphics = combinedCapture.createGraphics();
				graphics.drawImage(screenCapture, 0, 0, null);
				graphics.drawImage(fullScreen, 0, screenCapture.getHeight(), null);
				graphics.dispose();*/
				
			ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + ".png"));		
							
			// Wait for a moment to load the content
			Thread.sleep(2000); 
			
			
		     // Take a screenshot of the current visible part of the page
	        screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
	        fullScreen = ImageIO.read(screenshotFile);

	        // Get the current date and time
	        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd_HHmmss");
	        String datetime1 = dateFormat1.format(new Date());

	        // Create a new image with the current date and time displayed on it
	        BufferedImage combinedImage = new BufferedImage(fullScreen.getWidth(), fullScreen.getHeight() + 50, BufferedImage.TYPE_INT_ARGB);
	        combinedImage.getGraphics().drawImage(fullScreen, 0, 0, null);

	        // Draw the date and time on the image
	        combinedImage.getGraphics().drawString(datetime1, 10, fullScreen.getHeight() + 40);

	        // Save the combined image
	        File outputImageFile = new File("C:\\Users\\sneha\\Documents\\excel1\\" + combinedMethodName + "_" + Page + "_" + datetime + ".png");
	        ImageIO.write(combinedImage, "png", outputImageFile);
			
			i++;
		}
		
		private static void BSC(WebDriver driver, String address, String combinedMethodName) throws IOException, InterruptedException {
	    	
			Actions actions = new Actions(driver);
			
			int j = 1;
			String Page = String.format("%02d", j);
						    	
			WebElement search = driver.findElement(By.id("search-panel"));
			search.click();
			try {
				Thread.sleep(2000); // wait for 2 seconds
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
						    	
			search.clear(); // Clear any existing text in the search box
			search.sendKeys(address);      
			search.sendKeys(Keys.ENTER);
			try {
				Thread.sleep(5000); // wait for 2 seconds
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
	    	Date date = new Date();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        String datetime = dateFormat.format(date);	       
		       
			// Wait for a moment to load the content
			Thread.sleep(2000);
	        
	        // Take a screenshot of the current visible part of the page
	        File screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
	        BufferedImage fullScreen = ImageIO.read(screenshotFile);
	        ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + "_main.png"));	   
	        
			// Wait for a moment to load the content
			Thread.sleep(2000);
						       
			try {
						    		
				// Wait for the drop-down to appear
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
								        
				WebElement box = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".col-md-6.col-lg-4"))); 
				By dropDownItemsLocator = By.id("ContentPlaceHolder1_tokenbalance");
				wait.until(ExpectedConditions.visibilityOfElementLocated(dropDownItemsLocator));
				
				WebElement dropdownElement = box.findElement(dropDownItemsLocator);
				dropdownElement.click();
				try {
					Thread.sleep(5000); // wait for 5 seconds
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
								        	     
				// Get all the drop-down elements
				java.util.List<WebElement> dropdownItems = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".list-name.hash-tag.text-truncate, .additional-class, #additional-element")));
				try {
					Thread.sleep(2000); // Wait for 2 seconds to ensure the element is clickable
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
				
				int k = 2;
					        
				for (int i = 0; i < dropdownItems.size(); i++) {
					
					
					String page = String.format("%02d", k);
				    	    	
					dropdownItems = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".list-name.hash-tag.text-truncate, .additional-class, #additional-element")));
					try {
						Thread.sleep(2000); // Wait for 2 seconds to ensure the element is clickable
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
							    	    	
					WebElement item = dropdownItems.get(i);     	        
					try {
						Thread.sleep(500); // Wait for a short time after scrolling
					 } catch (InterruptedException e) {
						 e.printStackTrace();
					 }
				    	    	  	           	        
					// Wait for the item to be clickable
					wait.until(ExpectedConditions.elementToBeClickable(item));
					
					actions.moveToElement(item).perform(); 
					item.click();
					try {
						Thread.sleep(5000); // wait for 5 seconds
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					// Wait for a moment to load the content
					Thread.sleep(2000);			
					
					//Wait for a moment to load the content
					Thread.sleep(2000);
														
					WebElement token= wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".row.g-3.mb-4 > div:nth-child(1) > div > div > div:nth-child(2) > div > b")));
					String Tname = token.getText();
					try {
						Thread.sleep(2000); // wait for 5 seconds
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					 Tname = Tname.replaceAll("[^a-zA-Z0-9\\s.-]", "");
					 try {
	        			 Thread.sleep(2000); // wait for 5 seconds
	        		 } catch (InterruptedException e) {
	        			 e.printStackTrace();
	        		 }
									
					// Take a screenshot of the current visible part of the page
					screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
					fullScreen = ImageIO.read(screenshotFile);
					ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + Tname + "_" + combinedMethodName + "_" + page + "_" + datetime + "_inside.png"));
					
					// Wait for a moment to load the content
					Thread.sleep(2000);
								            
					// Go back to the page
					driver.navigate().back();
					try {
						Thread.sleep(1000); // wait for 2 seconds
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					k++;
				    	        
					wait = new WebDriverWait(driver, Duration.ofSeconds(30));
							    	        
					box = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".col-md-6.col-lg-4"))); 
							    	        
					dropDownItemsLocator = By.id("ContentPlaceHolder1_tokenbalance");
							    	        
					wait.until(ExpectedConditions.visibilityOfElementLocated(dropDownItemsLocator));  		
				} 
				        
			} catch (TimeoutException e) {			    		
				try {
					Thread.sleep(5000); // wait for 2 seconds
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}

	    		// Take a screenshot of the current visible part of the page
	    		screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
	    		fullScreen = ImageIO.read(screenshotFile);
	    		ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName +  "_" + Page + "_" + datetime + "_ouside.png"));
			} 
			
			j++;
		}		
					    
		private static void BTC(WebDriver driver, String address, String combinedMethodName ) throws IOException, InterruptedException {
			
			int i = 1;
			String Page = String.format("%02d", i);
	    	
			WebElement search = driver.findElement(By.cssSelector(".Input_input__13c07.SearchForm_input__1miNw.text-l.Input_addon-before-padding__2ZR1E.undefined.Input_font-size-sm__uGhyL"));
			search.click();
			try {
				Thread.sleep(2000); // wait for 2 seconds
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				    	
			search.clear(); // Clear any existing text in the search box
			search.sendKeys(address);      
			search.sendKeys(Keys.ENTER);
			try {
				Thread.sleep(5000); // wait for 2 seconds
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// Check if the security check page is present
			boolean isSecurityCheckPage = driver.getPageSource().contains("Checking if the site connection is secure");

			if (isSecurityCheckPage) {
			    // Handle the security check page
			   handleSecurityCheck();

			    try {
			        Thread.sleep(2000); // Wait for 2 seconds
			    } catch (InterruptedException e) {
			        e.printStackTrace();
			    }		    		   
			}
			
			WebElement link = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.search_hash__1vCzy")));
			link.click();
			try {
				Thread.sleep(5000); // wait for 2 seconds
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Thread.sleep(2000);
						    	
			// Check if the security check page is present
			isSecurityCheckPage = driver.getPageSource().contains("Checking if the site connection is secure");

			if (isSecurityCheckPage) {
			    // Handle the security check page
			   handleSecurityCheck();

			    try {
			        Thread.sleep(2000); // Wait for 2 seconds
			    } catch (InterruptedException e) {
			        e.printStackTrace();
			    }		    		   
			}
			
			Thread.sleep(2000); 
						        
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			String datetime = dateFormat.format(date);  	        
							        		       
			// Wait for a moment to load the content
			Thread.sleep(4000);  
					            
			// Take a screenshot of the current visible part of the page
			File screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage fullScreen = ImageIO.read(screenshotFile);
			ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + ".png"));
							
			// Wait for a moment to load the content
			Thread.sleep(2000); 
			
			i++;
			
		}		
					    

		private static void handleSecurityCheck() {
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofMinutes(1)); 
			
			driver.switchTo().frame(0);		 
			 
			// Find the checkbox element
			WebElement checkbox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.mark")));

			// Use Actions class to move to the element and then click
			Actions actions = new Actions(driver);
			actions.moveToElement(checkbox).click().perform();

	        // Switch back to the default content
	        driver.switchTo().defaultContent();
	        
			
			try {
			    Thread.sleep(2000); // Wait for 2 seconds
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
			
		}				    		
					    
		private static void BTG(WebDriver driver, String address, String combinedMethodName) throws IOException, InterruptedException {
			
			int i = 1;
			String Page = String.format("%02d", i);
	    	
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
	        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#search > div.control.has-icons-left > input")));
	    	search.click();
	    	try {
	    		Thread.sleep(2000); // wait for 2 seconds
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    	
	    	search.clear(); // Clear any existing text in the search box
	    	search.sendKeys(address);      
	    	search.sendKeys(Keys.ENTER);
	    	try {
	    		Thread.sleep(5000); // wait for 2 seconds
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}	
	        
	        Date date = new Date();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        String datetime = dateFormat.format(date);  	        
		        		       
		    // Wait for a moment to load the content
		    Thread.sleep(4000);  
		            
		    // Take a screenshot of the current visible part of the page
		    File screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		    BufferedImage fullScreen = ImageIO.read(screenshotFile);
		    ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_"  + datetime + ".png"));
		
		    // Wait for a moment to load the content
		    Thread.sleep(2000); 
		    
		    i++;
	    }				    
					    
		private static void DASH(WebDriver driver, String address, String combinedMethodName) throws IOException, InterruptedException {
			
			int i = 1;
			String Page = String.format("%02d", i);
	    	
	    	WebElement search = driver.findElement(By.id("searchField"));
	    	search.click();
	    	try {
	    		Thread.sleep(2000); // wait for 2 seconds
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    	
	    	search.clear(); // Clear any existing text in the search box
	    	search.sendKeys(address);      
	    	search.sendKeys(Keys.ENTER);
	    	try {
	    		Thread.sleep(5000); // wait for 2 seconds
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}	
	        
	        Date date = new Date();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        String datetime = dateFormat.format(date);  	        
		        		       
		    // Wait for a moment to load the content
		    Thread.sleep(4000);  
		            
		    // Take a screenshot of the current visible part of the page
		    File screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		    BufferedImage fullScreen = ImageIO.read(screenshotFile);
		    ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + ".png"));
		
		    // Wait for a moment to load the content
		    Thread.sleep(2000); 
		    
		    i++;
	    }				    
					    
		private static void EOS(WebDriver driver, String address, String combinedMethodName) throws IOException, InterruptedException {
			
			int i = 1;
			String Page = String.format("%02d", i);
	    	
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
	        WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("close-button-container")));
	        
	        WebElement close = popup.findElement(By.cssSelector("#top-container-home > div.ui.two.column.doubling.stackable.grid.container > div > div:nth-child(2) > div.modal-mask.p-2 > div > div > div.close-button-container > a > i"));
	    	close.click();
	    	try {
	    		Thread.sleep(2000); // wait for 2 seconds
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}

	       
	        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#search-input-field > input")));
	        search.click();
	        try {
	            Thread.sleep(5000); // wait for 5 seconds
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    	
	    	search.clear(); // Clear any existing text in the search box
	    	search.sendKeys(address);      
	    	search.sendKeys(Keys.ENTER);
	    	try {
	    		Thread.sleep(5000); // wait for 2 seconds
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}	
		        
	        Date date = new Date();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        String datetime = dateFormat.format(date);  	        
		        		       
		    // Wait for a moment to load the content
		    Thread.sleep(4000);  
		            
		    // Take a screenshot of the current visible part of the page
		    File screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		    BufferedImage fullScreen = ImageIO.read(screenshotFile);
		    ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + ".png"));
		
		    // Wait for a moment to load the content
		    Thread.sleep(2000);
		    
		    i++;
	    }				    
					    
		private static void ETC(WebDriver driver, String address, String combinedMethodName) throws IOException, InterruptedException {	
			
			int i = 1;
			String Page = String.format("%02d", i);
	    	
	    	WebElement search = driver.findElement(By.id("main-search-autocomplete"));
	    	search.click();
	    	try {
	    		Thread.sleep(2000); // wait for 2 seconds
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    	
	    	search.clear(); // Clear any existing text in the search box
	    	search.sendKeys(address);      
	    	search.sendKeys(Keys.ENTER);
	    	try {
	    		Thread.sleep(5000); // wait for 2 seconds
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}	
	    	        
	        Date date = new Date();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        String datetime = dateFormat.format(date);  	        
		        		       
		    // Wait for a moment to load the content
		    Thread.sleep(4000);  
		            
		    // Take a screenshot of the current visible part of the page
		    File screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		    BufferedImage fullScreen = ImageIO.read(screenshotFile);
		    ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + ".png"));
		
		    // Wait for a moment to load the content
		    Thread.sleep(2000); 
		    
		    i++;
	    }				    
					    
	    private static void ETH(WebDriver driver, String address, String combinedMethodName) throws IOException, InterruptedException {	 
	    	
	    	int j = 1;
			String Page = String.format("%02d", j);
	    	
	    	Actions actions = new Actions(driver);
	    	
	    	WebElement search = driver.findElement(By.id("search-panel"));
	    	search.click();
	    	try {
	    		Thread.sleep(2000); // wait for 2 seconds
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    	
	    	search.clear(); // Clear any existing text in the search box
	    	search.sendKeys(address);      
	    	search.sendKeys(Keys.ENTER);
	    	try {
	    		Thread.sleep(5000); // wait for 2 seconds
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}
	    	
	    	Date date = new Date();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        String datetime = dateFormat.format(date);	       
		       
			// Wait for a moment to load the content
			Thread.sleep(2000);
	        
	        // Take a screenshot of the current visible part of the page
	        File screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
	        BufferedImage fullScreen = ImageIO.read(screenshotFile);
	        ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + "_main.png"));	        
		       
			// Wait for a moment to load the content
			Thread.sleep(2000);
	        
	    	try {
	    		
	    		// Wait for the drop-down to appear
		        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		        
		        WebElement box = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".col-md-6.col-lg-4"))); 
		        By dropDownItemsLocator = By.id("dropdownMenuBalance");
		        wait.until(ExpectedConditions.visibilityOfElementLocated(dropDownItemsLocator));

		        WebElement dropdownElement = box.findElement(dropDownItemsLocator);
		        dropdownElement.click();	        
		        try {
					Thread.sleep(5000); // wait for 2 seconds
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	

	    	    // Get all the drop-down elements
	    	    java.util.List<WebElement> dropdownItems = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".list-name.hash-tag.text-truncate, .additional-class, #additional-element")));
		        try {
		            Thread.sleep(2000); // Wait for 2 seconds to ensure the element is clickable
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }  
		        
		        int k = 2;
		        
		        for (int i = 0; i < dropdownItems.size(); i++) {
		        	
		        	
					String page = String.format("%02d", k);
	    	    	
	    	    	dropdownItems = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".list-name.hash-tag.text-truncate, .additional-class, #additional-element")));
	    	        try {
	    	            Thread.sleep(2000); // Wait for 2 seconds to ensure the element is clickable
	    	        } catch (InterruptedException e) {
	    	            e.printStackTrace();
	    	        }
	    	    	
	    	        WebElement item = dropdownItems.get(i);     	        
	       	        try {
	       	            Thread.sleep(500); // Wait for a short time after scrolling
	       	        } catch (InterruptedException e) {
	       	            e.printStackTrace();
	       	        }
	    	    	  	       
	       	        
	    	        // Wait for the item to be clickable
	    	        wait.until(ExpectedConditions.elementToBeClickable(item));

	    	        actions.moveToElement(item).perform(); 
	    	        clickWithJavaScript(driver, item); 
	    	        try {
	    				Thread.sleep(5000); // wait for 2 seconds
	    			} catch (InterruptedException e) {
	    				e.printStackTrace();
	    			}	
			        
			        Thread.sleep(2000);
			        
			        try {
			        
			        	WebElement token= wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".row.g-3.mb-4 > div:nth-child(1) > div > div > div:nth-child(2) > div")));
			        				        				        
			        	if ( token.isEnabled()) {
			        					        
				        	String Tname = token.getText().trim();				        					    
				        	
				        	 if (!Tname.isEmpty()) {
						
				        		 // Split the text into lines
				        		 String[] lines = Tname.split("\n");
			            
				        		 String tokenSymbol = lines[1].trim();	           
				        		 try {
				        			 Thread.sleep(2000); // wait for 5 seconds
				        		 } catch (InterruptedException e) {
				        			 e.printStackTrace();
				        		 }					        		 				        		 				        		 
				        		 
				        		 String extractedURL = extractURL(tokenSymbol);
				        		 
				        		 if (isURL(extractedURL)) {
				        			 				        			 				        			 
				        			 	// Take a screenshot of the current visible part of the page
							        	screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
							        	fullScreen = ImageIO.read(screenshotFile);
							        	ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\NO-TOKEN-NAME_" + combinedMethodName + "_" + page + "_" + datetime + "_inside.png"));
						
							        	// Wait for a moment to load the page
							        	Thread.sleep(2000);
				        		
				                 } else {
				                	 
				                	 	tokenSymbol = tokenSymbol.replaceAll("[^a-zA-Z0-9\\s.-]", "");
			        
						        		 // Take a screenshot of the current visible part of the page
						        		 screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
						        		 fullScreen = ImageIO.read(screenshotFile);
						        		 ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + tokenSymbol + "_" + combinedMethodName + "_" + page + "_" + datetime + "_inside.png"));
					
						        		 // Wait for a moment to load the page
						        		 Thread.sleep(2000);			        	
				                 } 
				        	 }
			        	}
			        } catch (NoSuchElementException e) {
		            	e.printStackTrace();
		     
        			 	// Take a screenshot of the current visible part of the page
			        	screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
			        	fullScreen = ImageIO.read(screenshotFile);
			        	ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\NO_TOKEN_NAME_" + combinedMethodName + "_" + page + "_" + datetime + "_inside.png"));
		
			        	// Wait for a moment to load the page
			        	Thread.sleep(2000);	
				        				        	
			        }
	            
			        // Go back to the page
			        driver.navigate().back();   
		        
		            box = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".col-md-6.col-lg-4"))); 
		        
		            dropDownItemsLocator = By.id("dropdownMenuBalance");
		    	
		            wait.until(ExpectedConditions.visibilityOfElementLocated(dropDownItemsLocator));
		            try {
		            	Thread.sleep(5000); // wait for 2 seconds
		            } catch (InterruptedException e) {
		            	e.printStackTrace();
		            }
		            
		            k++;
		        } 

	    	} catch (TimeoutException e) {
			
	    		try {
	    			Thread.sleep(5000); // wait for 2 seconds
	    		} catch (InterruptedException ex) {
	    			ex.printStackTrace();
	    		}   		

	    		// Take a screenshot of the current visible part of the page
	    		screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
	    		fullScreen = ImageIO.read(screenshotFile);
	    		ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + "_ouside.png"));
	    	}
	    	
	    	j++;
	    }
	    
	 // Click the element using JavaScript to handle click intercept
	    private static void clickWithJavaScript(WebDriver driver, WebElement element) {
	        JavascriptExecutor executor = (JavascriptExecutor) driver;
	        executor.executeScript("arguments[0].click();", element);
	    }
	    
	    // Function to check if a given text is a URL
	    private static boolean isURL(String text) {
	        try {
	            new URL(text);
	            return true;
	        } catch (MalformedURLException e) {
	            return false;
	        }
	    }
	    
	    private static String extractURL(String text) {
	        // Use regular expression to find URLs within the text
	        Pattern pattern = Pattern.compile("https?://\\S+");
	        Matcher matcher = pattern.matcher(text);
	        if (matcher.find()) {
	            return matcher.group();
	        }
	        return null; // No URL found
	    }
					    
	    private static void ETHF(WebDriver driver, String address, String combinedMethodName) throws IOException, InterruptedException {
	    	
	    	int i = 1;
			String Page = String.format("%02d", i);
	    	
	    	WebElement search = driver.findElement(By.className("innerInput_XbdzX"));
	    	search.click();
	    	try {
	    		Thread.sleep(2000); // wait for 2 seconds
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    	
	    	search.clear(); // Clear any existing text in the search box
	    	search.sendKeys(address);      
	    	search.sendKeys(Keys.ENTER);
	    	try {
	    		Thread.sleep(5000); // wait for 2 seconds
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}	
	    	
	    	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
	    	wait.until(ExpectedConditions.numberOfWindowsToBe(2));
	    	 
	    	// Switch to the new window
	    	String originalWindowHandle = driver.getWindowHandle();
	    	for (String windowHandle : driver.getWindowHandles()) {
	    		
	    		if (!windowHandle.equals(originalWindowHandle)) {
	    			
	    			driver.switchTo().window(windowHandle);
	    			break;
	    		}
	    	}
	    	 
	        Date date = new Date();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        String datetime = dateFormat.format(date);  	        
		        		       
		    // Wait for a moment to load the content
		    Thread.sleep(4000);  
		            
		    // Take a screenshot of the current visible part of the page
		    File screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		    BufferedImage fullScreen = ImageIO.read(screenshotFile);
		    ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + ".png"));
		    
		    // Close the new window and switch back to the original window
		    driver.close();
		    driver.switchTo().window(originalWindowHandle);

		    // Wait for a moment to load the content on the original page
		    Thread.sleep(2000);
		    
		    i++;
	    }				    
					    
	    private static void ETHW(WebDriver driver, String address, String combinedMethodName) throws IOException, InterruptedException {

	    	int i = 1;
			String Page = String.format("%02d", i);
	    	
	    	WebElement search = driver.findElement(By.className("innerInput_XbdzX"));
	    	search.click();
	    	try {
	    		Thread.sleep(2000); // wait for 2 seconds
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    	
	    	search.clear(); // Clear any existing text in the search box
	    	search.sendKeys(address);      
	    	search.sendKeys(Keys.ENTER);
	    	try {
	    		Thread.sleep(5000); // wait for 2 seconds
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}	
	    	
	    	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
	    	wait.until(ExpectedConditions.numberOfWindowsToBe(2));
	    	 
	    	// Switch to the new window
	    	String originalWindowHandle = driver.getWindowHandle();
	    	for (String windowHandle : driver.getWindowHandles()) {
	    		
	    		if (!windowHandle.equals(originalWindowHandle)) {
	    			
	    			driver.switchTo().window(windowHandle);
	    			break;
	    		}
	    	}
	    	 
	        Date date = new Date();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        String datetime = dateFormat.format(date);  	        
		        		       
		    // Wait for a moment to load the content
		    Thread.sleep(4000);  
		            
		    // Take a screenshot of the current visible part of the page
		    File screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		    BufferedImage fullScreen = ImageIO.read(screenshotFile);
		    ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + ".png"));
		    
		    // Close the new window and switch back to the original window
		    driver.close();
		    driver.switchTo().window(originalWindowHandle);

		    // Wait for a moment to load the content on the original page
		    Thread.sleep(2000);
		    
		    i++;
	    }				    
					    
	    private static void FIL(WebDriver driver, String address, String combinedMethodName) throws IOException, InterruptedException {	 
	    	
	    	int i = 1;
			String Page = String.format("%02d", i);
	    	
	    	WebElement search = driver.findElement(By.xpath("//*[@id=\"__layout\"]/div/nav/div[2]/div[6]/input"));
	    	search.click();
	    	try {
	    		Thread.sleep(2000); // wait for 2 seconds
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    	
	    	search.clear(); // Clear any existing text in the search box
	    	search.sendKeys(address);      
	    	search.sendKeys(Keys.ENTER);
	    	try {
	    		Thread.sleep(5000); // wait for 2 seconds
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}	
	        
	        Date date = new Date();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        String datetime = dateFormat.format(date);  	        
		        		       
		    // Wait for a moment to load the content
		    Thread.sleep(4000);  
		            
		    // Take a screenshot of the current visible part of the page
		    File screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		    BufferedImage fullScreen = ImageIO.read(screenshotFile);
		    ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + ".png"));
		
		    // Wait for a moment to load the content
		    Thread.sleep(4000); 
		    
		    i++;
	    }				    
					    
	    private static void HDAC(WebDriver driver, String address, String combinedMethodName) throws IOException, InterruptedException {
	    	
	    	int i = 1;
			String Page = String.format("%02d", i);
	    	
	    	WebElement search = driver.findElement(By.id("search"));
	    	search.click();
	    	try {
	    		Thread.sleep(2000); // wait for 2 seconds
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    	
	    	search.clear(); // Clear any existing text in the search box
	    	search.sendKeys(address);   
	    	
	    	Thread.sleep(4000);
	    	 
	    	search.sendKeys(Keys.ENTER);
	    	try {
	    		Thread.sleep(5000); // wait for 2 seconds
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}	

	        Date date = new Date();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        String datetime = dateFormat.format(date);  	        
		        		       
		    // Wait for a moment to load the content
		    Thread.sleep(4000);  
		            
		    // Take a screenshot of the current visible part of the page
		    File screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		    BufferedImage fullScreen = ImageIO.read(screenshotFile);
		    ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + ".png"));
		
		    // Wait for a moment to load the content
		    Thread.sleep(2000); 
		    
		    i++;
	    }				    
					    
	    private static void KLAY(WebDriver driver, String address, String combinedMethodName) throws IOException, InterruptedException {
	    	
	    	int i = 1;
			String Page = String.format("%02d", i);
	    	
	    	WebElement search = driver.findElement(By.cssSelector(".Input__form.MainPage__searchForm"));
	    	search.click();
	    	try {
	    		Thread.sleep(2000); // wait for 2 seconds
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    	
	    	search.clear(); // Clear any existing text in the search box
	    	search.sendKeys(address);      
	    	search.sendKeys(Keys.ENTER);
	    	try {
	    		Thread.sleep(5000); // wait for 2 seconds
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}		    	

	    	 // Check if the target element is present on the opened page
	        try {
	        	WebElement box = driver.findElement(By.cssSelector(".MuiTabs-root.Tab__tabItemList.css-6wkrwl"));
	        	try {
	        		Thread.sleep(5000); // wait for 2 seconds
	        	} catch (InterruptedException e) {
	        		e.printStackTrace();
	        	}
	            WebElement TokenBalance = box.findElement(By.cssSelector(".MuiTabs-scroller.MuiTabs-hideScrollbar.MuiTabs-scrollableX.css-12qnib > div > button:nth-child(4)"));
	            
	         // Get the button's text content
	            String buttonText = TokenBalance.getText();

	            if ("Token Balance".equalsIgnoreCase(buttonText)) {
	                
	            	TokenBalance.click();
	            	try {
	    	    		Thread.sleep(2000); // wait for 2 seconds
	    	        } catch (InterruptedException e) {
	    	            e.printStackTrace();
	    	        }
	            }
	        } catch (NoSuchElementException e) {
	            
	        }
	        
	        Date date = new Date();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        String datetime = dateFormat.format(date);  	        
		        		       
		    // Wait for a moment to load the content
		    Thread.sleep(4000);  
		            
		    // Take a screenshot of the current visible part of the page
		    File screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		    BufferedImage fullScreen = ImageIO.read(screenshotFile);
		    ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + ".png"));
		
		    // Wait for a moment to load the content
		    Thread.sleep(2000); 
		    
		    i++;
	    }				    
					    
	    private static void LTC(WebDriver driver, String address, String combinedMethodName) throws IOException, InterruptedException {	
	    	
	    	Thread.sleep(5000);  
	    	
	    	int i = 1;
			String Page = String.format("%02d", i);
	    	
	    	WebElement search = driver.findElement(By.cssSelector("#search > div.control.has-icons-left > input"));
	    	search.click();
	    	try {
	    		Thread.sleep(2000); // wait for 2 seconds
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    	
	    	search.clear(); // Clear any existing text in the search box
	    	search.sendKeys(address);      
	    	search.sendKeys(Keys.ENTER);
	    	try {
	    		Thread.sleep(5000); // wait for 2 seconds
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}	
	        
	        Date date = new Date();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        String datetime = dateFormat.format(date);  	        
		        		       
		    // Wait for a moment to load the content
		    Thread.sleep(4000);  
		            
		    // Take a screenshot of the current visible part of the page
		    File screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		    BufferedImage fullScreen = ImageIO.read(screenshotFile);
		    ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + ".png"));
		
		    // Wait for a moment to load the content
		    Thread.sleep(2000); 
		    
		    i++;
	    }				    
					    
	    private static void QTUM(WebDriver driver, String address, String combinedMethodName) throws IOException, InterruptedException {
	    	
	    	int i = 1;
			String Page = String.format("%02d", i);
	    	
	    	WebElement search = driver.findElement(By.cssSelector("input[name='q'][type='text'][placeholder='Search your transaction, an address or a block']"));
	    	search.click();
	    	try {
	    		Thread.sleep(2000); // wait for 2 seconds
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    	
	    	search.clear(); // Clear any existing text in the search box
	    	search.sendKeys(address);      
	    	search.sendKeys(Keys.ENTER);			        
	    	try {
	    		Thread.sleep(2000); // wait for 2 seconds
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}	
	    
	        Date date = new Date();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        String datetime = dateFormat.format(date);  	        
		        		       
		    // Wait for a moment to load the content
		    Thread.sleep(4000);  
		            
		    // Take a screenshot of the current visible part of the page
		    File screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		    BufferedImage fullScreen = ImageIO.read(screenshotFile);
		    ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + "_main.png"));
		
		    // Wait for a moment to load the content
		    Thread.sleep(2000); 
		    
		    i++;
	    }				    
					    
	    private static void SOL(WebDriver driver, String address, String combinedMethodName) throws IOException, InterruptedException {
	    	
	    	int j = 1;
			String Page = String.format("%02d", j);
	    	
	    	Actions actions = new Actions(driver);
	    	
	    	WebElement search = driver.findElement(By.cssSelector("div[name='search'] input.ant-input"));
	    	search.click();
	    	try {
	    		Thread.sleep(2000); // wait for 2 seconds
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    	
	    	search.clear(); // Clear any existing text in the search box
	    	search.sendKeys(address);      
	    	search.sendKeys(Keys.ENTER);
	    	try {
	    		Thread.sleep(5000); // wait for 2 seconds
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}	

	    	Date date = new Date();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        String datetime = dateFormat.format(date);	
	        
		       
			// Wait for a moment to load the content
			Thread.sleep(2000);
	        
	        // Take a screenshot of the current visible part of the page
	        File screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
	        BufferedImage fullScreen = ImageIO.read(screenshotFile);
	        ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + "_main.png"));	        
		       
			// Wait for a moment to load the content
			Thread.sleep(4000);

	    	try {
	    		// Wait for the drop-down to appear or timeout
		    	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
		    	
		    	WebElement box = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-card")));
		    	try {
		            Thread.sleep(2000); // Wait for 2 seconds to ensure the element is clickable
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }  
		    	
		    	By dropdownItemSelector = By.cssSelector(".ant-dropdown-trigger");
		    	wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownItemSelector));
		    	try {
		            Thread.sleep(2000); // Wait for 2 seconds to ensure the element is clickable
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }  

		        WebElement dropdownElement = box.findElement(dropdownItemSelector);
		        dropdownElement.click();
		        try {
		            Thread.sleep(2000); // Wait for 2 seconds to ensure the element is clickable
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }  
		        
		        Thread.sleep(2000); // Wait for 1 second

	    	    // Get all the drop-down elements
	    	    java.util.List<WebElement> dropdownItems = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".sc-eCrewB.hJGsAq.text-ellipsis, .additional-class, #additional-element")));
	    	    try {
		            Thread.sleep(2000); // Wait for 2 seconds to ensure the element is clickable
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }   
	    	    
	    	    int k = 2;

	    	    for (int i = 0; i < dropdownItems.size(); i++) {
	    	    	
	    	    	
	    			String page = String.format("%02d", k);
	    	    	
	    	    	dropdownItems = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".sc-eCrewB.hJGsAq.text-ellipsis, .additional-class, #additional-element")));
	    	        try {
	    	            Thread.sleep(2000); // Wait for 2 seconds to ensure the element is clickable
	    	        } catch (InterruptedException e) {
	    	            e.printStackTrace();
	    	        }
	    	    	
	    	    	WebElement item = dropdownItems.get(i);
	    	    	try {
	       	            Thread.sleep(500); // Wait for a short time after scrolling
	       	        } catch (InterruptedException e) {
	       	            e.printStackTrace();
	       	        }
	    	    	
	    	    	String Tname = item.getText();					
					try {
						Thread.sleep(2000); // wait for 5 seconds
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    	    	
	    	    	wait.until(ExpectedConditions.elementToBeClickable(item));
	    	    	try {
			            Thread.sleep(2000); // Wait for 2 seconds to ensure the element is clickable
			        } catch (InterruptedException e) {
			            e.printStackTrace();
			        }  

	    	    	actions.moveToElement(item).perform();	    	    		    	    	
	     	        item.click();
	     	        try {
	     				Thread.sleep(5000); // wait for 2 seconds
	     			} catch (InterruptedException e) {
	     				e.printStackTrace();
	     			}	
	     	        
	    	        // Add a short delay or wait for a condition before taking a screenshot
	    	        Thread.sleep(1000); // Wait for 1 second		    	        

     	    	   	// Split the text into lines
	        		 String[] lines = Tname.split("\n");
            
	        		 String tokenSymbol = lines[1].trim();	           
	        		 try {
	        			 Thread.sleep(2000); // wait for 5 seconds
	        		 } catch (InterruptedException e) {
	        			 e.printStackTrace();
	        		 }					        		 
	        		 
	        		tokenSymbol = tokenSymbol.replaceAll("[^a-zA-Z0-9\\s.-]", "");
	        		
	        		Thread.sleep(1000);
	            
	    	        // Take a screenshot of the current visible part of the page
	    	        screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
	    	        fullScreen = ImageIO.read(screenshotFile);
	    	        ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + tokenSymbol + "_" + combinedMethodName + "_" + page + "_" + datetime + "_inside.png"));
	    	        
	    	        Thread.sleep(4000);

	    	        // Go back to the page
	    	        driver.navigate().back();

	    	        // Wait for a moment to load the page
	    	        Thread.sleep(2000);

	    	        box = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-card")));
	    	    	
	    	    	wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownItemSelector));

	    	        dropdownElement = box.findElement(dropdownItemSelector);
	    	        dropdownElement.click();
	    	        
	    	        try {
	    				Thread.sleep(5000); // wait for 2 seconds
	    			} catch (InterruptedException e) {
	    				e.printStackTrace();
	    			}
	    	        
	    	        // Wait for a moment to load the page
	    	        Thread.sleep(2000);
	    	        
	    	        k++;
		           
	    	    } 
	    
	    	} catch (TimeoutException e) {
	    		
	    		// Wait for a moment to load the page
	    		Thread.sleep(2000);
	        	
	    		// Take a screenshot of the current visible part of the page
	    		screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
	    		fullScreen = ImageIO.read(screenshotFile);
	    		ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + "_ouside.png"));
	    	}
	    	
	    	j++;
	    }				    
					    
	    private static void TRX(WebDriver driver, String address, String combinedMethodName) throws IOException, InterruptedException {	
	    	
	    	int i = 1;
			String Page = String.format("%02d", i);
	    	
	    	Actions actions = new Actions(driver);
	   	
	    	WebElement search = driver.findElement(By.cssSelector("#navSearchbarId > div > div.searchAndHotSec > div.row.justify-content-center.text-center > div > div > div > section > span > span > span.ant-input-affix-wrapper.ant-input-affix-wrapper-borderless > input"));
		   	search.click();
		   	try {
		   		Thread.sleep(2000); // wait for 2 seconds
		   	} catch (InterruptedException e) {
		           e.printStackTrace();
		   	}
		   	
		   	search.clear(); // Clear any existing text in the search box
		   	search.sendKeys(address);      
		   	search.sendKeys(Keys.ENTER);
		   	try {
		   		Thread.sleep(5000); // wait for 2 seconds
		   	} catch (InterruptedException e) {
		   		e.printStackTrace();
		   	}
	   	 
		   	Date date = new Date();
		   	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		   	String datetime = dateFormat.format(date);  	        
		        		       
		    // Wait for a moment to load the content
		    Thread.sleep(4000);  
		            
		    // Take a screenshot of the current visible part of the page
		    File screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		    BufferedImage fullScreen = ImageIO.read(screenshotFile);
		    ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + "_main.png"));
		    
		    WebElement details = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("asset-overview-table")));
		    WebElement more = details.findElement(By.className("ant-tabs-content-more-link"));
		    more.click();
		    
		    int pageNumber = 1;
	       
		    while (true) {       	            
	           	
		    	if (pageNumber == 1 ) {
		    		
		    		// Wait for a moment to load the content
		            Thread.sleep(2000);	
		            
		            i++;
					Page = String.format("%02d", i);
		            
		            // Take a screenshot of the current visible part of the page
		            screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		            fullScreen = ImageIO.read(screenshotFile);
		            ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + "_top.png"));
		
		            // Wait for a moment to load the content
		            Thread.sleep(2000); 
		            
		            // Scroll down to capture the middle part of the first page
		            actions.sendKeys(Keys.ARROW_DOWN).perform();
		            Thread.sleep(4000);	
		            try {
	                   Thread.sleep(2000); // Wait for 2 seconds to ensure the element is clickable
		            } catch (InterruptedException ex) {
	                   ex.printStackTrace();
		            }  
		            
		            i++;
					Page = String.format("%02d", i);
		            
		            // Take a screenshot of the current visible part of the page
		            screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		            fullScreen = ImageIO.read(screenshotFile);
		            ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + "_middle.png"));
		
		            // Wait for a moment to load the content
		            Thread.sleep(4000); 
		    
		            // Scroll down to capture the bottom part of the first page
		            actions.sendKeys(Keys.END).perform();
		            Thread.sleep(2000);
		            
		            i++;
					Page = String.format("%02d", i);
	       
		            // Take a screenshot of the current visible part of the page
		            screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		            fullScreen = ImageIO.read(screenshotFile);
		            ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + "_bottom.png"));
		
		            // Wait for a moment to load the content
		            Thread.sleep(2000); 
		            
		    	} else {
	       		
		    		// Scroll up to the top of the page for the next iteration
		    		while (true) {
	               		                	
		    			actions.sendKeys(Keys.PAGE_UP).perform();
		    			Thread.sleep(1000);
		    			
		    			// Check if the top of the page is reached
		    			JavascriptExecutor jsExecutors = (JavascriptExecutor) driver;
		    			boolean isAtTop = (boolean) jsExecutors.executeScript(
	                           "return (window.pageYOffset === 0);");
		    			if (isAtTop) {
		    				break;
		    			}
		    		}  
	               
		    		// Scroll up to the top of the page to ensure it starts from the top
		    		actions.sendKeys(Keys.HOME).perform();
		    		Thread.sleep(4000); 
		    		
		    		i++;
					Page = String.format("%02d", i);
	               
		    		// Take a screenshot of the current visible part of the page
		            screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		            fullScreen = ImageIO.read(screenshotFile);
		            ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\Account 과\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + "_top.png"));
		
		            // Wait for a moment to load the content
		            Thread.sleep(2000); 
		            
		            // Scroll down to capture the middle part of the first page
		            actions.sendKeys(Keys.PAGE_DOWN).perform();
		            Thread.sleep(4000);
		            
		            i++;
					Page = String.format("%02d", i);
		            
		            // Take a screenshot of the current visible part of the page
		            screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		            fullScreen = ImageIO.read(screenshotFile);
		            ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" +  Page + "_" + datetime + "_middle.png"));
		
		            // Wait for a moment to load the content
		            Thread.sleep(4000); 
		            
		            // Scroll down to capture the bottom part of the first page
		            actions.sendKeys(Keys.END).perform();
		            Thread.sleep(2000);
		            
		            i++;
					Page = String.format("%02d", i);
	               
		            // Take a screenshot of the current visible part of the page
		            screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		            fullScreen = ImageIO.read(screenshotFile);
		            ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page + "_" + datetime + "_bottom.png"));
		
		            // Wait for a moment to load the content
		            Thread.sleep(2000); 
		    	}
		    	
		    	// Check if pagination element is present
		    	WebElement paginationElement = driver.findElement(By.xpath("//ul[@class=\"ant-pagination ant-table-pagination ant-table-pagination-right"));

		    	if (!paginationElement.isDisplayed()) {
		    		
		    		// If pagination element is not displayed, break the loop (reached last page)
		    		break;
		    	}

		    	// Find the element for the next button or page numbers
		    	try {
		    		WebElement nextButton = driver.findElement(By.className("ant-pagination-item-link"));

	               	if (!nextButton.isEnabled()) {               	
	               		// If next button is not enabled, break the loop (reached last page)
	               		break;                   
	               	} else {
	               		nextButton.click();
	               		try {
	               			Thread.sleep(2000); // Wait for 2 seconds to ensure the element is clickable
	               		} catch (InterruptedException ex) {
	               			ex.printStackTrace();
	               		}                                                                        
	               	}              
	         	 } catch (NoSuchElementException e) {
	           	
	         		 String className = "ant-pagination-item ant-pagination-item-1 ant-pagination-item-active";  // The class attribute value
	         		 String tabIndex = "0";  // The tabindex attribute value
	           	
	         		 // If next button is not found, try clicking on individual page numbers one by one
	         		 WebElement pageNumberElement = driver.findElement(By.xpath("//li[@title='" + pageNumber + "' and contains(@class, '" + className + "') and @tabindex='" + tabIndex + "']"));
	               
	         		 if (!pageNumberElement.isDisplayed()) {
	               	
	         			 // If pagination element is not displayed, break the loop (reached last page)
	         			 break;
	                   
	         		 } else {
	         			 pageNumberElement.click();
	         			 try {
	         				 Thread.sleep(4000); // Wait for 2 seconds to ensure the element is clickable
	         			 } catch (InterruptedException ex) {
	         				 ex.printStackTrace();
	         			 }                                                      
	         		 }
	         	 }

		    	// Wait for a moment to load the content of the next page
		    	Thread.sleep(1000);

		    	// Increment the page number for the next iteration
		    	pageNumber++;
		    }
		    
		    i++;
	    }				    

	    private static void XRP(WebDriver driver, String address, String combinedMethodName) throws IOException, InterruptedException {	
	    	
	    	int i = 1;
			String Page = String.format("%02d", i);
	    	
	    	WebElement search = driver.findElement(By.className("react-autosuggest__input"));
	    	search.click();
	    	try {
	    		Thread.sleep(2000); // wait for 2 seconds
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    	
	    	search.clear(); // Clear any existing text in the search box
	    	search.sendKeys(address);      
	    	search.sendKeys(Keys.ENTER);
	    	try {
	    		Thread.sleep(5000); // wait for 2 seconds
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}			    	

	        Date date = new Date();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        String datetime = dateFormat.format(date);  	        
		        		       
		    // Wait for a moment to load the content
		    Thread.sleep(2000);  
		            
		    // Take a screenshot of the current visible part of the page
		    File screenshotFile = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
		    BufferedImage fullScreen = ImageIO.read(screenshotFile);
		    ImageIO.write(fullScreen, "png", new File("C:\\Users\\sneha\\Documents\\ExcelLink\\" + combinedMethodName + "_" + Page +  "_" + datetime + ".png"));
		
		    // Wait for a moment to load the content
		    Thread.sleep(2000); 
		    
		    i++;
	    }
					    
	}
