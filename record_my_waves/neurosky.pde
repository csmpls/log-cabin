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
   float pulse_easing = .1; 
  void initialize(PApplet parent, String com_port, PrintWriter log) {
    this.god = god;
    this.parent = parent;
    this.com_port = com_port;
    ns = new MindSet(parent);
    ns.connect(this.com_port);
  }
  
  int update() {
    
    try {
        med = ns.data.meditation; 
        attn = ns.data.attention; 
        headsetData = ns.getCurrentData();
     
      
      if (!has_initialized) {
        if (attn == -1.0)
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
  
  void set_attn_pulse() {
    attn_pulse += (attn - attn_pulse) * pulse_easing;
    attn_pulse = constrain(attn_pulse, 0.0, 100.0);
  }
  
  void set_med_pulse() {
    med_pulse += (med - med_pulse) * pulse_easing;
    med_pulse = constrain(med_pulse, 0.0, 100.0);
  }
  // returns an int[] with:
  // alpha1, alpha2, beta1, beta2, delta, gamma1, gamma2, theta, errorRate
  int[] get_raw_data_array() {
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
  String get_log_line(int[] raw) {
    Date currDate = new Date();
    String timestamp = sdf.format(currDate);
    String raw_line = "";
    for (int i = 0; i < raw.length; i++) {
      raw_line += "," + raw[i];
    }
    return timestamp + raw_line;
  }

}
