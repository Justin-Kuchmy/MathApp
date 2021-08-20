import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Program Name: MathAppView.java
 * Purpose: put something useful here
 * Coder: Justin Kuchmy
 * Date: Aug. 17, 2021
 */

public class MathAppView  extends JFrame
{

	private static final long serialVersionUID = 1L;
	public JTable table;
	public static String _dif = "";
	public MathAppView(TableModel model, String dif)
	{
		
		super("Current "+dif+" Records");
		 
		 //boiler plate
		 this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 this.setSize(650,400);
		 this.setLocationRelativeTo(null);
		 this.setLayout(new FlowLayout());
		 
		 //make the jtable and pass it model from the list
		 table = new JTable(model);
		 table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		 
		 TableColumnModel columnModel = table.getColumnModel();
		 columnModel.getColumn(0).setPreferredWidth(5);
		 columnModel.getColumn(1).setPreferredWidth(15);
		 columnModel.getColumn(3).setPreferredWidth(15);
		 JScrollPane scrollPane = new JScrollPane(table);
		 this.add(scrollPane);
	}
	public void updateViewDif(String dif)
	{
		_dif = dif;
	}
	 public void updateTable(TableModel model)
	 {

		 this.setTitle("Current "+_dif+" Records");
		 this.table.setModel(model);
	 }

}
//end class