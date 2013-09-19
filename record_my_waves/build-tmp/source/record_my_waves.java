import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.io.*; 
import processing.serial.*; 
import mindset.*; 
import java.util.Date; 
import java.text.SimpleDateFormat; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class record_my_waves extends PApplet {







Neurosky neurosky = new Neurosky();
String com_port = "/dev/tty.MindWave";

PrintWriter log; 

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");

public void setup() {
	try { log = new PrintWriter(new FileWriter("/Users/csmpls/my-great-neurolog.txt", true));  
	} catch (IOException e) { println("nope");}
	neurosky.initialize(this,com_port, log);
}

public void draw() {
	neurosky.update();
}


public void stop() {
	log.close();
}
/*
NEUROSKY
yes@cosmopol.is

hand-rolled in los angeles
august 2011

* * * / 

this class stores data from a neurosky mindset. 
it uses those data to calculate some in-house metrics:

float attn,
float med
  0-100 - e-sense attention/meditation score. 
  (these scores are produced by dark magic
  (ML) inside the neurosky API.)
  
float attn_pulse, 
float med_pulse
  0-100 - eased/smoothed version of attn
  and med. ideally, these values guard
  against the spikes we sometimes see
  in the the e-sense readings.
  
*/

public class Neurosky {
  PApplet parent;
  MindSet ns;
  
  String com_port;
  boolean god;
  
  float attn = 50;
  float med = 50;
  mindset.HeadsetData headsetData;
  
  float attn_pulse;
  float med_pulse;
  
  boolean is_meditating = false;
  boolean is_attentive = false;
  
  boolean has_initialized = false;

   float pulse_easing = .1f; 

  public void initialize(PApplet parent, String com_port, PrintWriter log) {
    this.god = god;
    this.parent = parent;
    this.com_port = com_port;
    ns = new MindSet(parent);
    ns.connect(this.com_port);
  }
  
  public int update() {
    
    try {
        med = ns.data.meditation; 
        attn = ns.data.attention; 
        headsetData = ns.getCurrentData();

     
      
      if (!has_initialized) {
        if (attn == -1.0f)
          return 1;
        else {
          if (attn < 20)  //hack: signal is overall low at beginning of stream 
            return 1;
            println("okay! i'm on!");
        }
          has_initialized=true;
      } else {
        set_attn_pulse();
        set_med_pulse();

        //write raw data to our log
try {        log.println(get_log_line(get_raw_data_array())); 
} catch (Exception e) { println("error printing log this round");}

      }
      
      
      
      
    } catch( ArrayIndexOutOfBoundsException e ) {
        return 1;
      }
     
     return 0;
    }
  
  public void set_attn_pulse() {
    attn_pulse += (attn - attn_pulse) * pulse_easing;
    attn_pulse = constrain(attn_pulse, 0.0f, 100.0f);
  }
  
  public void set_med_pulse() {
    med_pulse += (med - med_pulse) * pulse_easing;
    med_pulse = constrain(med_pulse, 0.0f, 100.0f);
  }

  // returns an int[] with:
  // alpha1, alpha2, beta1, beta2, delta, gamma1, gamma2, theta, errorRate
  public int[] get_raw_data_array() {
    int[] raw = { headsetData.alpha1,
      headsetData.alpha2,
      headsetData.beta1,
      headsetData.beta2,
      headsetData.delta,
      headsetData.gamma1,
      headsetData.gamma2,
      headsetData.theta,
      headsetData.errorRate};
    return raw;
  }

  public String get_log_line(int[] raw) {
    Date currDate = new Date();
    String timestamp = sdf.format(currDate);

    String raw_line = "";
    for (int i = 0; i < raw.length; i++) {
      raw_line += "," + raw[i];
    }

    return timestamp + raw_line + "\n";
  }


}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "record_my_waves" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
