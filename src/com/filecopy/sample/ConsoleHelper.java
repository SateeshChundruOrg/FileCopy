package com.filecopy.sample;

public class ConsoleHelper {
    private static boolean  killExecution=false;
    private static String  message="";
    static Thread thread=null;
    
    public static  void kill(String message1) {
    	message=message1;
    	killExecution=true;
    	try {
			thread.interrupt();
			thread.join(); 
		} catch (Exception e) {
		}
        
    }


    public  static void animate() {
    	killExecution=false;
    	thread=new Thread(()->{
    		while (true) {
    			if(killExecution) {
    				 System.out.println(message);
    				break;
    			}
    			 System.out.print(".");
    			 try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
    		}
    	} );
    	thread.start();
    		
          }

   
}