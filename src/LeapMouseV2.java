import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.State;
import com.leapmotion.leap.Gesture.Type;

import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Leap Motion Sensor Mouse. Created at Nhacks 2016.
 * @author Hossein Ahmadian-Yazdi
 * @version March 12, 2016
 */


class MyListener extends Listener{
	public Robot robot;


	public void onInit (Controller c){
		System.out.println("Initialized!");

	}

	public void onExit(Controller c){
		System.out.println("Exited.");
	}

	public void onConnect(Controller c){
		System.out.println("Connected!");
		
		// Setting the policies and the gestures that the leap motion should recognise
		c.setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
		c.setPolicy(Controller.PolicyFlag.POLICY_IMAGES);
		c.enableGesture(Gesture.Type.TYPE_CIRCLE);
		c.enableGesture(Gesture.Type.TYPE_SWIPE);
		
		// Configuring the setting for the circle gesture
		c.config().setFloat("Gesture.Circle.MinRadius", 10.0f);
		c.config().save();
	}
	
	/**
	 * This method sets up the frame by frame which the leap motion will be detecting.
	 * @Param c - This is the leap motion controller we pass in.
	 */
	public void onFrame(Controller c){		
		try{robot = new Robot();}catch (Exception e){}
		Frame frame = c.frame();
		InteractionBox box = frame.interactionBox();
		
		// For loop which goes through each finger on the hand which the 
		// Leap motion detects.
		for (Finger f : frame.fingers()){
			// If the finger it detects is our index finger, then it will create
			// a box around our finger and with some calculations, have the 
			// cursor follow our finger tip.
			if (f.type() == f.type().TYPE_INDEX){
				Vector fingerPos = f.stabilizedTipPosition();
				Vector boxFingerPos = box.normalizePoint(fingerPos);
				Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
				robot.mouseMove((int)(screen.width * boxFingerPos.getX()), (int)(screen.height - boxFingerPos.getY()*screen.getHeight()));
			}
		}	

		
		// This for loop goes through each hand on our body.
		for (Hand h : frame.hands()){
			
			// If the hand it detects is our right hand.
			if (h.isRight()){
				
				// Then it checks to see if the distance between my index is less than 
				// or equal to 40 (pinch) and as well that my my palms Z position
				// is either over the leap motion or in front.
				if (h.pinchDistance() <= 40 && h.palmPosition().getZ() <= 0){
					
					// Then, it will take this as a single click.
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					try { Thread.sleep(1500); } catch(Exception e) {}
				}
				
				// Otherwise if our fingers are in a pinch position however we are behind
				// the leap motion senssor, then this is detected as double click.
				else if (h.pinchDistance()<= 40 && h.palmPosition().getZ() > 0){
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					try { Thread.sleep(1500); } catch(Exception e) {}
				}
				
				// If our hand is in a fist position, then we want to drag the mouse.
				if (h.grabStrength() >= 1){
					robot.mousePress(InputEvent.BUTTON1_MASK);
				}
				
				// Otherwise we we want to release the mouse.
				else{
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
				}

			}

		}

		
		// This for loop goes through all the possible gestures that can be performed
		// Ex. Swiping motion, Circular motion, etc.
		for (Gesture g : frame.gestures()){
			
			// If the gesture we are doing is a swipe with our hand
			// As well that the swipe is in the beginning state
			if (g.type() == Type.TYPE_SWIPE && g.state() == State.STATE_START){
				SwipeGesture swipe = new SwipeGesture(g);				
				
				// Check to see if we are swiping up or down 
				// and accordingly scroll the screen up or down.
				if (swipe.direction().normalized().getY() < 0){
					robot.mouseWheel(-1);
					try { Thread.sleep(800); } catch(Exception e) {}
				}
				else{
					robot.mouseWheel(1);
					try { Thread.sleep(800); } catch(Exception e) {}

				}
			}
			
			// Otherwise we will be doing a circular motion with our finger.
			// As well that our circle is at the end state 
			else if (g.type() == Type.TYPE_CIRCLE && g.state() == State.STATE_STOP){
				CircleGesture circle = new CircleGesture(g);
				
				// Check to see if we are rotating clockwise or counter clockwise.
				// If we are rotating clockwise, then we want to zoom into the page.
				// We use Ctrl+Scroll In to acheieve this.
				if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI/2) {
					robot.keyPress(KeyEvent.VK_CONTROL);
					robot.mouseWheel(-1);
					robot.keyRelease(KeyEvent.VK_CONTROL);
					try { Thread.sleep(3000); } catch(Exception e) {}
				}
				
				// Otherwise we are rotating counterclockwise
				// Which we will zoom out of the page.
				// We use Ctrl+Mouse Wheel scroll towards ourselves.
				else{
					robot.keyPress(KeyEvent.VK_CONTROL);
					robot.mouseWheel(1);
					robot.keyRelease(KeyEvent.VK_CONTROL);
					try { Thread.sleep(3000); } catch(Exception e) {}
				}
				
			}

		}


	}
}


