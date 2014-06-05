

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Packets.SoundPacket;


public class Record  extends JFrame{

	private Client client;
	protected boolean running;
	ByteArrayOutputStream out;
	private byte audio [];
	private Record rc;
	private JButton capture = new JButton("Capture");
	private JButton stop = new JButton("Stop");
	private JButton play = new JButton("Play");
	private JButton send = new JButton("Send");
	private JPanel panel = new JPanel();
	
	public Record(Client client){
		super("Record");
		
		this.rc = this;
		
		this.client = client;
		capture.setEnabled(true);
		capture.addActionListener(new CaptureListener());
		
		stop.addActionListener(new StopListener());
	    stop.setEnabled(false);
	    
	    play.addActionListener(new PlayListener());
	    play.setEnabled(false);
	    
	    send.addActionListener(new SendListener());
	    send.setEnabled(false);
	    
	    
	    panel.add(capture);
	    panel.add(stop);
	    panel.add(play);
	    panel.add(send);
	    
	    
	    
	    this.setContentPane(panel);
	    this.pack();
	    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    this.setVisible(true);
	}
	
	private void captureAudio(){
		try{
			final AudioFormat format = getFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			final TargetDataLine line = (TargetDataLine)AudioSystem.getLine(info);
			line.open(format);
			line.start();
			Runnable runner = new Runnable() {
				int bufferSize = (int)format.getSampleRate() * format.getFrameSize();
				byte buffer[] = new byte[bufferSize];
				public void run() {
					out = new ByteArrayOutputStream();
					running = true;
					try{
						while(running){
							int count = line.read(buffer, 0, buffer.length);
							if(count > 0) {
								out.write(buffer, 0, count);
							}
						}
						audio = out.toByteArray();
						out.close();
					} catch(IOException e){
						System.err.println("I/O problems: " + e);
			            System.exit(-1);
					}
				}
			};
			Thread captureThread = new Thread(runner);
			captureThread.start();
		}catch(LineUnavailableException e){
			System.err.println("Line unavailable: " + e);
		    System.exit(-2);
		}
	}
	
	private AudioFormat getFormat(){
		float sampleRate = 8000;
		int sampleSizeInBits = 8;
	    int channels = 1;
	    boolean signed = true;
	    boolean bigEndian = true;
	    
	    return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
		
	}

	private void playAudio(){
		try{
			InputStream input = new ByteArrayInputStream(audio);
			final AudioFormat format = getFormat();
			final AudioInputStream ais = new AudioInputStream(input, format, audio.length / format.getFrameSize());
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			final SourceDataLine line = (SourceDataLine)AudioSystem.getLine(info);
		    line.open(format);
		    line.start();
		    
		    Runnable runner = new Runnable() {
		    	int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
		        byte buffer[] = new byte[bufferSize];
		        
				public void run() {
					try{
						int count;
						while((count = ais.read(buffer, 0, buffer.length)) != -1){
							if(count > 0){
								line.write(buffer, 0, count);
							}
						}
						line.drain();
						line.close();
					} catch(IOException e){
						System.err.println("Line unavailable: " + e);
					    System.exit(-3);
					}
					
				}
			};
			Thread playThread = new Thread(runner);
		    playThread.start();
		} catch(LineUnavailableException e){
			System.err.println("Line unavailable: " + e);
		    System.exit(-4);
		}
	}
	
	class CaptureListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			capture.setEnabled(false);
	        stop.setEnabled(true);
	        play.setEnabled(false);
	        captureAudio();
		}
		
	}
	
	class StopListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			
			running = false;
			capture.setEnabled(true);
	        stop.setEnabled(false);
	        play.setEnabled(true);
	        send.setEnabled(true);	        
		}
		
	}

	class PlayListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			playAudio();
		}
		
	}
	
	class SendListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			
			  SoundPacket sp = new SoundPacket (audio, client.getUser());    
			  client.sendPacket(sp);
			  rc.dispose();
		}
		
	}
	

}



