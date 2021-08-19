import java.awt.Button;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.TableModel;
import javax.swing.JOptionPane;

/**
 * Program Name: MathAppController.java Purpose: put something useful here
 * Coder: Justin Kuchmy Date: Aug. 17, 2021
 */

public class MathAppController extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JLabel ProblemText, InputQuestions;
	private TextField answerHere, numOfQs;
	private JPanel Input1, Input2;
	private Button next, submit, records, startBtn, reset;
	private int pblmNum = 0;
	private int Questions = 0;
	private int num1 = 0, num2 = 0;
	public long start;
	public double elapsedSeconds;
	private MathAppView view;

	public MathAppController()
	{
		super("Justins Math App");

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(400, 200);
		this.setLocationRelativeTo(null);
		this.setLayout(new GridLayout(7, 1));

		// create JPanels
		Input1 = new JPanel();
		Input1.setLayout(new GridLayout(1, 2));

		Input2 = new JPanel();
		Input2.setLayout(new GridLayout(1, 2));

		// Create swing components
		InputQuestions = new JLabel("How Many Questions");
		numOfQs = new TextField("");
		numOfQs.setEnabled(true);

		ProblemText = new JLabel("Question");
		answerHere = new TextField("");
		answerHere.setEnabled(false);

		submit = new Button("Check");
		submit.setEnabled(false);

		next = new Button("Next");
		next.setEnabled(false);

		startBtn = new Button("Start");
		records = new Button("View Records");
		
		reset = new Button("Reset");
	

		// add components
		Input1.add(InputQuestions);
		Input1.add(numOfQs);
		Input2.add(ProblemText);
		Input2.add(answerHere);
		this.add(Input1);
		this.add(Input2);
		this.add(startBtn);
		this.add(submit);
		this.add(next);
		this.add(records);
		this.add(reset);

		// Create Action Listener
		CheckData Data = new CheckData();
		next.addActionListener(Data);
		submit.addActionListener(Data);
		startBtn.addActionListener(Data);
		reset.addActionListener(Data);

		GetRecord Records = new GetRecord();
		records.addActionListener(Records);
		this.setVisible(true);
	}

	public String calcTime(double seconds)
	{
		double temp = seconds;
		int hour = 0, min = 0, second = 0;
		min = (int) (temp / 60.0);
		second = (int) (temp - (min * 60.0));
		String result = String.format("%02d:%02d:%02d", hour, min, second);
		return result;
	}

	public void randomNums()
	{
		int min = 10, max = 100;
		int range = max - min + 1;
		double test1 = (Math.random() * range + min);
		double test2 = (Math.random() * range + min);
		num1 = (int) (test1);
		num2 = (int) (test2);
		pblmNum++;
		ProblemText.setText("Question " + pblmNum + ": " + num1 + " x " + num2);
	}

	public class CheckData implements ActionListener
	{

		public void actionPerformed(ActionEvent e)
		{

			if (pblmNum <= Questions)
			{
				if (e.getActionCommand().equals("Start"))
				{
					if (numOfQs.getText().equals(""))
					{
						JOptionPane.showMessageDialog(null, "Please How Many Questions.");
						numOfQs.requestFocus();
						return;
					} else
					{
						Questions = Integer.parseInt(numOfQs.getText());
						numOfQs.setEnabled(false);
						next.setEnabled(false);
						submit.setEnabled(true);
						answerHere.setEnabled(true);
						startBtn.setEnabled(false);
						answerHere.requestFocus();
						randomNums();
						start = System.currentTimeMillis();
					}
				}
				if (e.getActionCommand().equals("Check"))
				{
					Integer result = num1 * num2;
					String answer = result.toString();
					if (answerHere.getText().equals(answer))
					{
						next.setEnabled(true);
						submit.setEnabled(false);
						submit.setBackground(Color.GREEN);

					} else
					{
						submit.setBackground(Color.RED);
						JOptionPane.showMessageDialog(null, "Wrong Try again");
						answerHere.setText("");
						next.setEnabled(false);
						answerHere.requestFocus();
						
					}
				}
				if (e.getActionCommand().equals("Next"))
				{
					next.setEnabled(false);
					submit.setEnabled(true);
					submit.setBackground(null);
					if (pblmNum < Questions)
					{
						randomNums();

					} else
					{
						next.setEnabled(true);
						submit.setEnabled(false);
						answerHere.setEnabled(false);
						next.setLabel("Submit Time");
						elapsedSeconds = (System.currentTimeMillis() - start) / 1000.0;
						ProblemText.setText("Completed in: " + elapsedSeconds);
						System.out.println("Time: " + elapsedSeconds);
					}
					answerHere.setText("");
					answerHere.requestFocus();

				}
				if (e.getActionCommand().equals("Submit Time"))
				{

					System.out.println("Submitted to the database");
					Connection myConn = null;
					Statement myStmt = null;
					PreparedStatement myPrepStmt = null;
					try
					{
						myConn = DriverManager.getConnection(
								"jdbc:mySql://localhost:3306/mathapp?useSSL=false&allowPublicKeyRetrieval=true", "root",
								"password");
						myStmt = myConn.createStatement();
						myPrepStmt = myConn.prepareStatement(
								"INSERT INTO Records(TimeTaken, DateSubmitted, Questions, avgTimePerQ)"
										+ "VALUES( ? , now(), ? , ? );");
						// assign the JTextField inputs to the placeholders
						myPrepStmt.setString(1, calcTime(elapsedSeconds).toString());
						myPrepStmt.setString(2, String.valueOf(Questions));
						myPrepStmt.setString(3, String.valueOf(elapsedSeconds / Questions));

						myPrepStmt.executeUpdate();

					} catch (SQLException e1)
					{
						System.out.println("SQL Exeption, message is: " + e1.getMessage());
					} catch (Exception ex)
					{
						System.out.println("Some other Exception, message is: " + ex.getMessage());
					} finally
					{
						try
						{
							// standard clean up code to make sure connection to DB is closed
							if (myStmt != null)
								myStmt.close();
							if (myConn != null)
								myConn.close();
						} catch (SQLException ex)
						{
							System.out.println("SQL Exception INSIDE finally block: " + ex.getMessage());
							ex.printStackTrace();
						} //catch

					} //try
				} //Submit time
				if (e.getActionCommand().equals("Reset"))
				{
					numOfQs.setText("");
					numOfQs.setEnabled(true);
					next.setEnabled(false);
					submit.setEnabled(false);
					answerHere.setEnabled(false);
					startBtn.setEnabled(true);
					ProblemText.setText("Question");
					next.setLabel("Next");
					Questions = 0;
					pblmNum = 0;
					numOfQs.requestFocus();
				}

			} // if current Q is less than Max 

		} //action preformed

	} //check data

	public class GetRecord implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (e.getActionCommand().equals("View Records"))
			{

				Connection myConn = null;
				Statement myStmt = null;
				ResultSet myRslt = null;
				PreparedStatement myPrepStmt = null;
				try
				{
					myConn = DriverManager.getConnection(
							"jdbc:mySql://localhost:3306/mathapp?useSSL=false&allowPublicKeyRetrieval=true", "root",
							"password");
					myStmt = myConn.createStatement();
					myPrepStmt = myConn.prepareStatement("select * from Records order by 4, 2, 5;");
					myRslt = myPrepStmt.executeQuery();

					// call method in Dbutils and pass it to myrslt object
					TableModel model = DbUtils.resultSetToTableModel(myRslt);

					if (view != null) // If there is currently another View open
					{
						view.updateTable(model); // update the table
					} else
					{
						// create a View object and pass model to the view
						// and catch the returned JFrame Object which holds the JTable
						view = new MathAppView(model);
						view.setVisible(true);
					}

				} catch (SQLException e1)
				{
					System.out.println("SQL Exeption, message is: " + e1.getMessage());
				} catch (Exception ex)
				{
					System.out.println("Some other Exception, message is: " + ex.getMessage());
				} finally
				{
					try
					{
						// standard clean up code to make sure connection to DB is closed
						if (myRslt != null)
							myRslt.close();
						if (myStmt != null)
							myStmt.close();
						if (myConn != null)
							myConn.close();
					} catch (SQLException ex)
					{
						System.out.println("SQL Exception INSIDE finally block: " + ex.getMessage());
						ex.printStackTrace();
					}

				}
			}
		}
	}

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		new MathAppController();

	}

}
//end class