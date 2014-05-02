package wator;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.Timer;

import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 * @author David Matuszek
 * @author Mike Ciul mciul@ldc.upenn.edu
 * @version April 29, 2014
 */
public class Wator extends JFrame {
	private static final long serialVersionUID = 1L;
	private Wator watorGui;
	private WatorModel model;

	private WorldView canvas;
	private JPanel titledCanvas;
	private JTextArea programTextArea;
	private JScrollPane scrollableProgramTextArea;
	private JPanel controlPanel;
	private JPanel sliderPanel;
	private JTextField statusField;
	private JSlider speedControlSlider;
	private JSlider fishPopSlider;
	private JSlider sharkPopSlider;
	private JSlider fishBirthSlider;
	private JSlider sharkBirthSlider;
	private JSlider sharkStarveSlider;
	private JPanel inputs;

	private JMenuItem loadMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem saveAsMenuItem;

	private JButton pauseButton;
	private JButton resetButton;

	private static final int OCEAN_WIDTH = 150;
	private static final int OCEAN_HEIGHT = 120;
	private static final int INITIAL_CANVAS_WIDTH = OCEAN_WIDTH*4;
	private static final int INITIAL_CANVAS_HEIGHT = OCEAN_HEIGHT*4;

	private ActionListener clock;
	private Timer timer;
	private int period=1000;

	/**
	 * Constructor for a Logo interpreter.
	 */
	public Wator() {
		setTitle("Wator");
		watorGui = this;
		model = new WatorModel(OCEAN_WIDTH, OCEAN_HEIGHT);
		clock = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.gc();
				canvas.repaint();
				model.animate();
				
			}
		};
		timer = null;
	}

	/**
	 * Starts the application.
	 * 
	 * @param args
	 *            Ignored.
	 */
	public static void main(String[] args) {
		new Wator().setup();
	}

	/**
	 * Creates and displays the GUI, then executes the interpreter.
	 */
	public void setup() {
		createGui();
		pack();
		setVisible(true);
		reset();
		timer = new Timer(period, clock);
	}

	public void reset() {
		Shark.setGestationTime(sharkBirthSlider.getValue());
		Shark.setInitialEnergy(sharkStarveSlider.getValue());
		Fish.setGestationTime(fishBirthSlider.getValue());
		model.reset(fishPopSlider.getValue(), sharkPopSlider.getValue());
		changeSpeed();
		canvas.repaint();
	}

	/**
	 * Lays out the GUI
	 */
	private void createGui() {
		createComponents();
		arrangeComponents();
		attachListeners();
		repaint();
	}

	/**
	 * Creates all Components used by the GUI.
	 */
	private void createComponents() {
		pauseButton = new JButton("Start");
		resetButton = new JButton("Reset");
		statusField = new JTextField();//

		setTitle("Wator");
		canvas = new WorldView(model, INITIAL_CANVAS_WIDTH,
				INITIAL_CANVAS_HEIGHT);
		canvas.setBackground(Color.BLACK);
		titledCanvas = new JPanel();
		titledCanvas.setPreferredSize(new Dimension(INITIAL_CANVAS_WIDTH + 20,
				INITIAL_CANVAS_HEIGHT + 40));
		titledCanvas.setLayout(new BorderLayout());
		titledCanvas.add(canvas, BorderLayout.CENTER);
		addTitledBorder(titledCanvas, "World map");

		/*
		 * programTextArea = new JTextArea(30, 30); scrollableProgramTextArea =
		 * makeScrollable(programTextArea, "Logo program");
		 */
		controlPanel = new JPanel();
		sliderPanel = new JPanel();
		createSpeedSlider();
		createFishPopSlider();
		createSharkPopSlider();
		createFishBirthSlider();
		createSharkBirthSlider();
		createSharkStarveSlider();
	}

	/**
	 * Creates the speed control.
	 */
	private void createSpeedSlider() {
		speedControlSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 10, 5);
		speedControlSlider.setMajorTickSpacing(3);
		speedControlSlider.setMinorTickSpacing(1);
		speedControlSlider.setPaintTicks(true);
		speedControlSlider.setPaintLabels(true);
	}

	private void createFishPopSlider() {
		fishPopSlider = new JSlider(SwingConstants.HORIZONTAL, 1,
				oceanSize() / 20, oceanSize()/500+1);
		fishPopSlider.setMajorTickSpacing(5 * minorTicks(fishPopSlider));
		fishPopSlider.setMinorTickSpacing(minorTicks(fishPopSlider));
		fishPopSlider.setPaintTicks(true);
		fishPopSlider.setPaintLabels(true);
	}

	private void createSharkPopSlider() {
		sharkPopSlider = new JSlider(SwingConstants.HORIZONTAL, 1,
				oceanSize() / 20, oceanSize()/80+1);
		sharkPopSlider.setMajorTickSpacing(5 * minorTicks(sharkPopSlider));
		sharkPopSlider.setMinorTickSpacing(minorTicks(sharkPopSlider));
		sharkPopSlider.setPaintTicks(true);
		sharkPopSlider.setPaintLabels(true);
	}

	private int oceanSize() {
		return model.getWidth() * model.getHeight();
	}

	private int minorTicks(JSlider slider) {
		final int range = slider.getMaximum() - slider.getMinimum();
		final int count = 20;
		final double rough = 1.0 * range / count;
		final double roughLog = Math.log(rough) / Math.log(5);
		return (int) Math.pow(5, (int) roughLog + 1);
	}

	private void createFishBirthSlider() {
		fishBirthSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 40, 5);
		fishBirthSlider.setMajorTickSpacing(5);
		fishBirthSlider.setMinorTickSpacing(1);
		fishBirthSlider.setPaintTicks(true);
		fishBirthSlider.setPaintLabels(true);
	}

	private void createSharkBirthSlider() {
		sharkBirthSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 40, 10);
		sharkBirthSlider.setMajorTickSpacing(5);
		sharkBirthSlider.setMinorTickSpacing(1);
		sharkBirthSlider.setPaintTicks(true);
		sharkBirthSlider.setPaintLabels(true);
	}

	private void createSharkStarveSlider() {
		sharkStarveSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 40, 10);
		sharkStarveSlider.setMajorTickSpacing(5);
		sharkStarveSlider.setMinorTickSpacing(1);
		sharkStarveSlider.setPaintTicks(true);
		sharkStarveSlider.setPaintLabels(true);
	}

	/**
	 * 
	 */
	private void arrangeComponents() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		this.setJMenuBar(menuBar);

		setLayout(new BorderLayout());

		add(titledCanvas, BorderLayout.CENTER);
		add(sliderPanel, BorderLayout.EAST);
		add(controlPanel, BorderLayout.SOUTH);

		layoutSliderPanel();
		layoutControlPanel();

		setBackground(Color.WHITE);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void layoutSliderPanel() {
		sliderPanel.setLayout(new GridLayout(0, 2));

		sliderPanel.add(new JLabel("          Speed:"));
		sliderPanel.add(speedControlSlider);

		sliderPanel.add(new JLabel("           Fish:"));
		sliderPanel.add(fishPopSlider);
		sliderPanel.add(new JLabel("  Fish Birth Time:"));
		sliderPanel.add(fishBirthSlider);

		sliderPanel.add(new JLabel("         Sharks:"));
		sliderPanel.add(sharkPopSlider);
		sliderPanel.add(new JLabel("  Shark Birth Time:"));
		sliderPanel.add(sharkBirthSlider);
		sliderPanel.add(new JLabel("  Shark Starve Time:"));
		sliderPanel.add(sharkStarveSlider);
	}

	private void layoutControlPanel() {
		inputs = new JPanel();

		controlPanel.setLayout(new BorderLayout());
		controlPanel.add(inputs, BorderLayout.CENTER);
		controlPanel.add(statusField, BorderLayout.SOUTH);

		inputs.add(pauseButton);
		inputs.add(resetButton);
	}

	/**
	 * Attach listeners to all GUI components that need them.
	 */
	private void attachListeners() {

		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if ("Pause".equals(pauseButton.getText())) {
					pauseButton.setText("Start");
					displayStatus("Wator is paused.");
					timer.stop();
				} else {
					pauseButton.setText("Pause");
					displayStatus("");
					timer.setDelay(period);
					timer.start();
				}
			}
		});

		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				reset();
			}
		});

		// Set speed
		speedControlSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				changeSpeed();
			}
		});

		// Set fish breeding
		fishBirthSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Fish.setGestationTime(fishBirthSlider.getValue());
			}
		});

		// Set shark breeding
		sharkBirthSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Shark.setGestationTime(sharkBirthSlider.getValue());
			}
		});
		
		// Set shark starving
		sharkStarveSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Shark.setInitialEnergy(sharkStarveSlider.getValue());
			}
		});

		// canvas.addComponentListener(new ComponentAdapter() {
		// public void componentResized(ComponentEvent e) {
		// System.out.println("repainting");
		// //canvas.repaint();
		// }
		// });
	}

	/**
	 * Adds a titled border to the given Component. According to the Swing
	 * documentation, the <code>setBorder(Border border)</code> does not work
	 * well with all types of JComponents.
	 * 
	 * @param component
	 *            The component to which a titled forder is to be added.
	 * @param title
	 *            The text used for the title.
	 */
	private void addTitledBorder(JComponent component, String title) {
		Border lineBorder = BorderFactory.createLineBorder(Color.black);
		TitledBorder titledBorder = BorderFactory.createTitledBorder(
				lineBorder, title);
		component.setBorder(titledBorder);
	}

	/**
	 * Changes the speed at which the turtle draws, to correspond to the speed
	 * set by the speed control slider. A speed of zero means no movement, while
	 * the maximum speed available on the slider means that there is no delay
	 * between turtle actions.
	 * 
	 * @param speed
	 */
	private void changeSpeed() {
		int speed = speedControlSlider.getValue();
		period = 1000 / speed;
		if (timer!=null) {
			timer.setDelay(period);
		}
	}

	void displayStatus(String status) {
		statusField.setText(status);
	}
}