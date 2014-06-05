import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;

import UserManager.User;


public class MouseAdapter implements MouseListener {

	private ClientGUI gui;

	public MouseAdapter(ClientGUI gui) {
		this.gui = gui;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount()==2){
			JList userList = gui.getUserList();
			User user = ((User)userList.getSelectedValue());
			new ProfileFrame(user);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
