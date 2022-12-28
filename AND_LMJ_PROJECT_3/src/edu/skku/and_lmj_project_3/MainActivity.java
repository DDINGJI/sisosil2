package edu.skku.and_lmj_project_3;

import edu.skku.lmjjni.FullcolorledJNI;
import edu.skku.lmjjni.DotmatrixJNI;
import edu.skku.lmjjni.LedJNI;
import edu.skku.lmjjni.PiezoJNI;
import edu.skku.lmjjni.TextLcdJNI;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.os.SystemClock;

public class MainActivity extends Activity {
	
	BackThread thread = new BackThread();
	boolean start = false, restart = false;	
	boolean alive = true;
	boolean playStar = false;
	boolean playAirplane = false;
	boolean playSame = false;
	int speed = 130;
	int score = 0;
	private char ledData = (char)0;
	
	private LedJNI ledJNI = new LedJNI();
	private PiezoJNI piezoJNI = new PiezoJNI();
	private TextLcdJNI textlcdJNI = new TextLcdJNI();
	private DotmatrixJNI dotmatrixJNI = new DotmatrixJNI();
	private FullcolorledJNI fullcolorledJNI = new FullcolorledJNI();
	
	
	private int buttonIds[] = {
			R.id.button7, R.id.button6, R.id.button5, R.id.button4, R.id.button3, R.id.button2, R.id.button1 };
	private Button [] buttons = new Button[7];
	private TextView difView;
	private TextView judgeView;
	private TextView scoreView;
	
	private char pvalues[] = { 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17};
	long startTime = 0;
	long touchTime = 0;
	long eTime = 0;
	int sameCnt = 0;
	int airplaneCnt = 0;
	int starCnt = 0;
	
	@Override
	protected void onResume() {
		piezoJNI.open();
		textlcdJNI.on();
		super.onResume();
	}

	@Override
	protected void onPause() {
		piezoJNI.close();
		textlcdJNI.off();
		super.onPause();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//FPGA initializations
		textlcdJNI.initialize();
		textlcdJNI.clear();
		textlcdJNI.print1Line("Please select a");
		textlcdJNI.print2Line("song you want");
		ledJNI.on((char)0);//All off
		fullcolorledJNI.FLEDControl(5, 0, 0, 0);//All off
		
		// Start thread
		thread.setDaemon(true);
		thread.start();
		
		for (int i = 0; i<7; i++) {
			buttons[i] = (Button)findViewById(buttonIds[i]);
			findViewById(buttonIds[i]).setOnClickListener(buttonListener);
		}
		
		difView = (TextView)findViewById(R.id.textView3);
		judgeView = (TextView)findViewById(R.id.textView4);
		scoreView = (TextView)findViewById(R.id.textView5);

     	findViewById(R.id.radio0).setOnClickListener(radioButtonListener);
     	findViewById(R.id.radio1).setOnClickListener(radioButtonListener);
     	findViewById(R.id.radio2).setOnClickListener(radioButtonListener);
     	
	}
	
 	RadioButton.OnClickListener radioButtonListener = new RadioButton.OnClickListener(){
 		
    	public void onClick(View v) {
    		startTime = SystemClock.elapsedRealtime();
			
			if (start) {
				restart = true;
				start = false;
			} else {
				start = true;
				restart = true;
			}
    		
    		switch(v.getId()) {
    		case R.id.radio0 :
    			playSame = true;
    			textlcdJNI.clear();
				textlcdJNI.print1Line("Now playing...");
				textlcdJNI.print2Line("What is same?");
    			break;
    		case R.id.radio1 :
    			playAirplane = true;
    			textlcdJNI.clear();
				textlcdJNI.print1Line("Now playing...");
				textlcdJNI.print2Line("Airplane");
    			break;
    		case R.id.radio2 :
    			playStar = true;
    			textlcdJNI.clear();
				textlcdJNI.print1Line("Now playing...");
				textlcdJNI.print2Line("Little star");
    			break;
    		}

    	}
    };
    
	Button.OnClickListener buttonListener = new Button.OnClickListener(){
	 		
	    	public void onClick(View v) {
	    		int bId = 0;
	    		
	    		switch(v.getId()) {
	    		case R.id.button7 :
	    			bId = 0;
	    			break;
	    		case R.id.button6 :
	    			bId = 1;
	    			break;
	    		case R.id.button5 :
	    			bId = 2;
	    			break;
	    		case R.id.button4 :
	    			bId = 3;
	    			break;
	    		case R.id.button3 :
	    			bId = 4;
	    			break;
	    		case R.id.button2 :
	    			bId = 5;
	    			break;
	    		case R.id.button1 :
	    			bId = 6;
	    			break;
	    		}
	    		
	    		
	    		touchTime = SystemClock.elapsedRealtime();
	    		
	    		//piezo
				piezoJNI.write( pvalues[bId]);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				piezoJNI.write((char)0);
	    		
				eTime = touchTime - startTime;
				scoreView.setText(Long.toString(eTime));
				boolean isMiss = true;
				
				//perfect or good
				if (playSame == true){
					sameCnt++;
					
					long dif = eTime - samePerfect[bId][0];
					long tmp;
					
					for (int i = 1; i<samePerfect[bId].length; i++) {
						tmp = eTime - samePerfect[bId][i];
						if (Math.abs(tmp) < Math.abs(dif)) dif = tmp;
					}
					
					difView.setText(Long.toString(dif));
					if (-100 < dif && dif < 100) {
						//Perfect
						judgeView.setText("Perfect");
						score += 100;
						fullcolorledJNI.FLEDControl(5, 0, 0, 100);
						isMiss = false;
					}
					else if (-250 < dif && dif < 250){
						//Good
						judgeView.setText("Good");
						score += 60;
						fullcolorledJNI.FLEDControl(5, 0, 100, 0);
						isMiss = false;
					}
				}
				else if (playAirplane == true) {
					airplaneCnt++;
					
					long dif = eTime - airplanePerfect[bId][0];
					long tmp;
					
					for (int i = 1; i<airplanePerfect[bId].length; i++) {
						tmp = eTime - airplanePerfect[bId][i];
						if (Math.abs(tmp) < Math.abs(dif)) dif = tmp;
					}
					
					difView.setText(Long.toString(dif));
					if (-100 < dif && dif < 100) {
						//Perfect
						judgeView.setText("Perfect");
						score += 100;
						fullcolorledJNI.FLEDControl(5, 0, 0, 100);
						isMiss = false;
					}
					else if (-250 < dif && dif < 250){
						//Good
						judgeView.setText("Good");
						score += 60;
						fullcolorledJNI.FLEDControl(5, 0, 100, 0);
						isMiss = false;
					}
				}
				else if (playStar == true){
					starCnt++;
					long dif = eTime - starPerfect[bId][0];
					long tmp;
					
					for (int i = 1; i<starPerfect[bId].length; i++) {
						tmp = eTime - starPerfect[bId][i];
						if (Math.abs(tmp) < Math.abs(dif)) dif = tmp;
					}
					
					difView.setText(Long.toString(dif));
					if (-100 < dif && dif < 100) {
						//Perfect
						judgeView.setText("Perfect");
						score += 100;
						fullcolorledJNI.FLEDControl(5, 0, 0, 100);
						isMiss = false;
					}
					else if (-250 < dif && dif < 250){
						//Good
						judgeView.setText("Good");
						score += 60;
						fullcolorledJNI.FLEDControl(5, 0, 100, 0);
						isMiss = false;
					}
						
				}
				
				//Miss
				if (isMiss == true){
					judgeView.setText("Miss");
					fullcolorledJNI.FLEDControl(5, 100, 0, 0);
				}
				
				//Song finished
				if (sameCnt == 20 || airplaneCnt == 25 || starCnt == 42) {
					
					if (sameCnt == 20) {
						score /= 20;
						sameCnt = 0;
					}
					else if (airplaneCnt == 25) {
						score /= 25;
						airplaneCnt = 0;
					}
					else if(starCnt == 42) {
						score /= 42;
						starCnt = 0;
					}
					scoreView.setText(Integer.toString(score) + "Á¡");

					//Led
					int ledNum = (int) (score/12.5);
					for (int i = 0; i < ledNum; i++) {
						ledData |= (0x80 >> i);
					}
					ledJNI.on(ledData);
					
					//Textlcd
					textlcdJNI.clear();
					String rating = "Rating: " + Integer.toString(ledNum) + "/8";
					textlcdJNI.print1Line(rating);
					textlcdJNI.print2Line("Score: " + Integer.toString(score));
					
					ledData = (char)0;					
					score = 0;
				}

			}
	};
	
	class BackThread extends Thread {
		public void run() {
			while (alive) {
				if (!start) {
					// do nothing
				}
				else {
					
					if (playStar == true) {
						for (int i = 0; i < (starSheet.length() - 18) / 2; i++) {
							for (int j = 0; j < speed; j++) {
								dotmatrixJNI.DotMatrixControl(starSheet.substring(2 * i, 2 * i + 20));
							}
						}
						playStar = false;
						start = false;
					}
					else if (playAirplane == true) {
						
						for (int i = 0; i < (airplaneSheet.length() - 18) / 2; i++) {
							for (int j = 0; j < speed; j++) {
								dotmatrixJNI.DotMatrixControl(airplaneSheet.substring(2 * i, 2 * i + 20));
							}
						}
						playAirplane = false;
						start = false;
					}
					else if (playSame == true) {
						for (int i = 0; i < (sameSheet.length() - 18) / 2; i++) {
							for (int j = 0; j < speed; j++) {
								dotmatrixJNI.DotMatrixControl(sameSheet.substring(2 * i, 2 * i + 20));
							}
						}
						playSame = false;
						start = false;
					}
				}
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	String starSheet = "00000000000000000000404004040202040008081010202040000404080810102000040408081010200040400404020204000808101020204000000000000000000000";
	String airplaneSheet = "0000000000000000000010204020101010002020200010040400102040201010100020201020400000000000000000000000";
	String sameSheet = "0000000000000000000040100440100402020204000008080810101020202040000000000000000000000000";
	
	
	int starPerfect[][] = {
			{5000, 5500, 12000, 21020, 21520, 28020},
			{11000, 11500, 16000, 20010, 27020, 27520},
			{10000, 10500, 15000, 15500, 19010, 19510, 26020, 26520},
			{9000, 9500, 14010, 14500, 18010, 18510, 25020, 25520},
			{6000, 6500, 8000, 13000, 13500, 17010, 17510, 22020, 22520, 24020},
			{7000, 7500, 23020, 23520},
			{0}
	};
	
	int airplanePerfect[][] = {
			{6000, 14000, 19020},
			{5500, 6500, 9000, 9500, 10000, 13500, 14500, 17000, 17500, 18510},
			{5000,7000, 7500, 8000, 11000, 13000, 15000, 15500, 16000, 18010},
			{0},
			{11500, 12000},
			{0},
			{0}
	};
	
	int samePerfect[][] = {
			{5000, 6500, 15500},
			{14000, 14500, 15000},
			{5500, 7000, 12500, 13000, 13500},
			{11000, 11500, 12000},
			{6000, 7500, 9500},
			{8000, 8500, 9000},
			{0}
	};
	
}
