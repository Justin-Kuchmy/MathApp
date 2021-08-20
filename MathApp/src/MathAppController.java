import java.awt.Button;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.TableModel;
import javax.swing.JOptionPane;

/**
 * Program Name: MathAppController.java 
 * Purpose: Used to practice more difficult math problems in my head. 
 * Coder: Justin Kuchmy Date: Aug. 17, 2021
 */

public class MathAppController extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JLabel ProblemText, InputQuestions;					
	private TextField answerHere, numOfQs;						
	private JPanel Input1, Input2;								
	private Button next, submit, records, startBtn, reset;		 
	private int pblmNum = 0;									// keeps track of current question
	private int Questions = 0;									// number of questions entered
	private int num1 = 0, num2 = 0;								// holds the 2 random numbers
	private long start;    										// Start Time		
	private double elapsedSeconds;								// End time, Result				
	private MathAppView view;	 								// Displays Database
	private int difficulty = 0; 								// 0, 1, 2 or 3 for Easy, medium, hard, mixed
	
	// Array of Options
	private String[] options = new String[] { "Easy", "Medium", "Hard", "Mixed" };
	
	// Datanbase Generation Scripts.
	private static final String CREATE_DATABASE_SQL = "CREATE DATABASE IF NOT EXISTS mathapp";
	private static final String CREATE_TABLE_SQL_1 =  "Create table IF NOT EXISTS EasyRecords(id int primary key auto_increment, TimeTaken TIME, DateSubmitted DateTime, Questions int, avgTimePerQ float);";
	private static final String CREATE_TABLE_SQL_2 =  "create table IF NOT EXISTS MediumRecords(id int primary key auto_increment, TimeTaken TIME, DateSubmitted DateTime, Questions int, avgTimePerQ float); ";
	private static final String CREATE_TABLE_SQL_3 =  "create table IF NOT EXISTS HardRecords(id int primary key auto_increment, TimeTaken TIME, DateSubmitted DateTime, Questions int, avgTimePerQ float); ";
	private static final String CREATE_TABLE_SQL_4 =  "create table IF NOT EXISTS MixedRecords(id int primary key auto_increment, TimeTaken TIME, DateSubmitted DateTime, Questions int, avgTimePerQ float);";
	private String dbURL = "jdbc:mySql://localhost:3306/mathapp?useSSL=false&allowPublicKeyRetrieval=true";
	private String dbURL2 = "jdbc:mySql://localhost:3306/";	
	private String username = "root";
	private String password = "password";
	public MathAppController()
	{
		super("In Your Head Math");

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Toolkit tk = Toolkit.getDefaultToolkit();  
		float xSize = ((int) tk.getScreenSize().getWidth()*0.2f);  
		float ySize = ((int) tk.getScreenSize().getHeight()*0.2f);  
		this.setSize((int)xSize, (int)ySize);
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
		choiceMethod();

	}

	/*Name: choiceMethod
	 *Purpose: After Launching the app, The user selects a difficulty to determine the math question difficulty. 
	 *Accepts: void
	 *Returns: void
	 */
	public void choiceMethod()
	{
		difficulty = JOptionPane.showOptionDialog(null, "Select a Difficulty", "Difficulty Selector", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		
	}
	/*Name:  calcTime
	 *Purpose: Calculates the time in seconds between when the user clicks start and submit time, at the end. 
	 *Accepts: Double Seconds
	 *Returns: String
	 */
	public String calcTime(double seconds)
	{
		double temp = seconds;
		int hour = 0, min = 0, second = 0;
		min = (int) (temp / 60.0);
		second = (int) (temp - (min * 60.0));
		String result = String.format("%02d:%02d:%02d", hour, min, second);
		return result;
	}
	/*Name: randomNums
	 *Purpose: Based on the difficulty picked, numbers between 10 and 1000 are randomly selected as math problems. 
	 *Accepts: String 
	 *Returns: void
	 */
	public void randomNums(String dif)
	{
		int min1 = 0, max1, min2 = 0, max2;
		int range1 = 0, range2 = 0;
		double test1 = 0.0;
		double test2 = 0.0;
		boolean mix = false;
		switch (dif) {
		case "Easy":
			min1 = 10;
			min2 = 10;
			max1 = 100;
			max2 = 100;
			range1 = max1 - min1 + 1;
			range2 = max2 - min2 + 1;
			break;
		case "Medium":
			min1 = 100;
			min2 = 10;
			max1 = 1000;
			max2 = 100;
			range1 = max1 - min1 + 1;
			range2 = max2 - min2 + 1;
			break;
		case "Hard":
			min1 = 100;
			min2 = 100;
			max1 = 1000;
			max2 = 1000;
			range1 = max1 - min1 + 1;
			range2 = max2 - min2 + 1;
			break;
		case "Mixed":
			min1 = 10;
			max1 = 100;
			min2 = 100;
			max2 = 1000;
			range1 = max1 - min1 + 1;
			range2 = max2 - min2 + 1;
			mix = true;
			break;
		}
		double result1;
		double result2;
		if (mix) //if user selected mix
		{
			//Used to create a 50% change to get a 2 digit number vs a 3 digit number. 
			//normally you would have a 2 digit number 10% of the time. 
			Random r = new Random();
			result1 = (r.nextInt(2) + 1); //randomly picks a 1 or a 2 
			if (result1 < 0)
				result1 *= -1;
			if ((int) result1 == 1)
			{
				test1 = (Math.random() * range1 + min1);
			} else
			{
				test1 = (Math.random() * range2 + min2);
			}

			result2 = (r.nextInt(2) + 1);
			if (result2 < 0)
				result2 *= -1;
			if ((int) result2 == 1)
			{
				test2 = (Math.random() * range1 + min1);
			} else
			{
				test2 = (Math.random() * range2 + min2);
			}
		}
		else
		{
			test1 = (Math.random() * range1 + min1);
			test2 = (Math.random() * range2 + min2);
		}
		num1 = (int) (test1);
		num2 = (int) (test2);
		pblmNum++;
		ProblemText.setText("Question " + pblmNum + ": " + num1 + " x " + num2); //display the question on the screen.
		
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
						randomNums(options[difficulty]);
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
						randomNums(options[difficulty]);

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

					
					Connection myConn = null;
					Statement myStmt = null;
					PreparedStatement myPrepStmt = null;
					try
					{
						//mathapp?useSSL=false&allowPublicKeyRetrieval=true
						myConn = DriverManager.getConnection(dbURL2, username,password); // has the url without the databse info
						myStmt = myConn.createStatement();
						myStmt.executeUpdate(CREATE_DATABASE_SQL); //create the database
						myStmt.close();
						myConn.close();
						myConn = DriverManager.getConnection(dbURL, username,password); //included data with the new db name
						myStmt = myConn.createStatement();
						myStmt.executeUpdate(CREATE_TABLE_SQL_1);
						myStmt.executeUpdate(CREATE_TABLE_SQL_2);
						myStmt.executeUpdate(CREATE_TABLE_SQL_3);
						myStmt.executeUpdate(CREATE_TABLE_SQL_4);
						
						myPrepStmt = myConn.prepareStatement(
								"INSERT INTO "+options[difficulty]+"Records(TimeTaken, DateSubmitted, Questions, avgTimePerQ)"
										+ "VALUES( ? , now(), ? , ? );");
						// assign the JTextField inputs to the placeholders
						myPrepStmt.setString(1, calcTime(elapsedSeconds).toString());
						myPrepStmt.setString(2, String.valueOf(Questions));
						myPrepStmt.setString(3, String.valueOf(elapsedSeconds / Questions));

						myPrepStmt.executeUpdate();
						System.out.println("Submitted to the database");

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
							next.setEnabled(false);
						} catch (SQLException ex)
						{
							System.out.println("SQL Exception INSIDE finally block: " + ex.getMessage());
							ex.printStackTrace();
						} // catch

					} // try
					
				} // Submit time
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
					choiceMethod(); //Select a new difficulty. 
					view.updateViewDif(options[difficulty]); //	change the View window "title" to match the new 
															//selected difficulty. 
					numOfQs.requestFocus();

				}

			} // if current Q is less than Max

		} // action performed

	} // check data

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
					myConn = DriverManager.getConnection(dbURL, username,password);
					myStmt = myConn.createStatement();
					myPrepStmt = myConn.prepareStatement("select * from "+options[difficulty]+"Records order by 4, 2, 5;");
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
						view = new MathAppView(model, options[difficulty]);
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