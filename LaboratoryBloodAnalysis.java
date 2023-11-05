package FinalPDraft;

/**
 * author @Kyle
 * 22 January 2023
 * ICS Final Project
 */

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.URI;


public class LaboratoryBloodAnalysis extends JFrame implements ActionListener {

    public static void main(String[] args) {
        String Kiarash_Data = null;
        LaboratoryBloodAnalysis dashboardScreen = new LaboratoryBloodAnalysis(); //  create a frame for us
        dashboardScreen.setVisible(true); // make frame visible
        // java look and feel library for professional look
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JLabel lblHeader, lblRecentResults, lblAbnormalResults;
    private JTextArea txtRecentResults, txtAbnormalResults;
    private JScrollPane spRecentResults, spAbnormalResults;
    private JButton btnRefresh;
    private JComboBox<String> cbPatients;

    //    private JMenuBar btnPatientsList;
    private JMenuItem patientItem;

    //---------------------------------------------     FRONT END     ---------------------------------------------\\

    public LaboratoryBloodAnalysis() {

        // set the title and layout of the frame
        setTitle("Patient Dashboard");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exit out of application
        ImageIcon image = new ImageIcon("blood-test-icon.ico"); // import icon
        setIconImage(image.getImage()); // set icon of the JFrame (dashboard)

//        // Create the background label
//        ImageIcon backgroundImage = new ImageIcon("/Users/kiarashsotoudeh/Documents/School Files/Computer Science/Final/Final Project - Blood Test Analyzer (FBS Full Edition)/KyleNeonBG.png");
//        JLabel backgroundLabel = new JLabel(backgroundImage);
//        backgroundLabel.setSize(getSize());
//        backgroundLabel.setOpaque(false);
//        setContentPane(backgroundLabel);

        // create a menu bar
        JMenuBar menuBar = new JMenuBar();

        // create a "File" menu
        JMenu patients_list = new JMenu("Patients list");
        JMenu send_the_results = new JMenu("Send the results");
        JMenu edit_the_results = new JMenu("Edit the results");

        // create a "Exit" menu item
        JMenuItem nextCSV = new JMenuItem("Next CSV File");
        JMenuItem sending = new JMenuItem("Send the results to the receptionist");
        JMenuItem editing = new JMenuItem("Edit the results manually");

        patients_list.add(nextCSV);
        send_the_results.add(sending);
        edit_the_results.add(editing);

        //  exitItem.addActionListener(e -> System.exit(0));
        nextCSV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "No other CSV files to show.", "Warning",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        sending.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to send the results to the receptionist?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    String results = txtAbnormalResults.getText();
                    String mailTo = "kiarashsotoudeh@gmail.com";
                    String subject = "Blood Test Results";
                    String body = results;

                    try {
                        URI mailToURI = new URI("mailto", mailTo + "?subject=" + subject + "&body=" + body, null);
                        Desktop.getDesktop().mail(mailToURI);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "An error occurred while trying to open the email client", "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (URISyntaxException ex) {
                        JOptionPane.showMessageDialog(null, "An error occurred while trying to compose the email", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        editing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtAbnormalResults.setEditable(true);
            }
        });

        // add the menu bar option 1 to the action listener to connect to the back end :D
        patients_list.addActionListener(this);

        // add the "File" menu to the menu bar
        menuBar.add(patients_list);
        menuBar.add(send_the_results);
        menuBar.add(edit_the_results);

        // add the menu bar to the JFrame
        setJMenuBar(menuBar);

        // JComboBox variable
        cbPatients = new JComboBox<>();
        cbPatients.setBackground(Color.WHITE); // background colour of the text in the dropdown menu
        cbPatients.setForeground(Color.decode("#011725")); // text colour of the dropdown menu
        cbPatients.setSize(150, 25);
        cbPatients.setLocation(300, 100);
        cbPatients.setFont(new Font("Optima", Font.BOLD, 12));

        // Create a JLabel for the title
        JLabel lblTitle = new JLabel("Patients List");
        lblTitle.setFont(new Font("Papyrus", Font.BOLD | Font.ITALIC, 18));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setHorizontalAlignment(JLabel.CENTER);

        this.add(cbPatients);
        JPanel dropdownbox = new JPanel();
        dropdownbox.setLayout(new BorderLayout());
        dropdownbox.add(lblTitle, BorderLayout.CENTER);
        dropdownbox.setBackground(Color.decode("#0C2942"));
        dropdownbox.add(cbPatients, BorderLayout.WEST);
        dropdownbox.setBorder(new CompoundBorder(new LineBorder(Color.decode("#001728"), 20, false), new EmptyBorder(5,
                5,5,
                5)));
        this.add(dropdownbox, BorderLayout.WEST);

        // create the header label
        JPanel panelHeader = new JPanel();
        lblHeader = new JLabel("Patient Test Results");
        lblHeader.setFont(new Font("Papyrus", Font.BOLD | Font.ITALIC, 25));
        panelHeader.setLayout(new FlowLayout(FlowLayout.CENTER)); // aligns the panel header to the centre :P
        panelHeader.add(lblHeader);
        // panelHeader.setBorder(new LineBorder(Color.decode("#242424"), 10, true)); // makes rounded header
        panelHeader.setBackground(Color.decode("#0C2942")); // header colour
        lblHeader.setForeground(Color.white);
        add(panelHeader, BorderLayout.PAGE_START);

        // create the recent and abnormal results label and text area
        lblRecentResults = new JLabel("Recent Results:");
        lblRecentResults.setFont(new Font("Papyrus", Font.BOLD | Font.ITALIC, 18)); // set font for text boxes
        lblRecentResults.setForeground(Color.WHITE); // colour of "Recent Results:"

        lblAbnormalResults = new JLabel("Results Analysis:");
        lblAbnormalResults.setFont(new Font("Papyrus", Font.BOLD | Font.ITALIC, 18)); // set font for text boxes
        lblAbnormalResults.setForeground(Color.WHITE); // colour of "Results Analysis:"

        // create the text box
        txtRecentResults = new JTextArea(15, 35); // dimensions of the text box for recent results
        txtRecentResults.setFont(new Font("Chalkboard SE", Font.PLAIN, 14)); // set font for text boxes
        txtRecentResults.setBackground(Color.decode("#0D253B")); // recent results box
        txtRecentResults.setForeground(Color.decode("#C2E6F9"));
        txtRecentResults.setLineWrap(true);
        txtRecentResults.setWrapStyleWord(true);
        txtRecentResults.setEditable(false);

        txtAbnormalResults = new JTextArea(15, 35); // dimensions of the text box for the abnormal results
        txtAbnormalResults.setFont(new Font("Chalkboard SE", Font.PLAIN, 14)); // set font for text boxes
        txtAbnormalResults.setEditable(false);
        txtAbnormalResults.setLineWrap(true);
        txtAbnormalResults.setWrapStyleWord(true);
        txtAbnormalResults.setBackground(Color.decode("#0D253B")); // analysis/abnormal results box
        txtAbnormalResults.setForeground(Color.decode("#C2E6F9")); // changes the colour of the text panels

        spRecentResults = new JScrollPane(txtRecentResults);
        spAbnormalResults = new JScrollPane(txtAbnormalResults);

        // create the "get the results" button
        btnRefresh = new JButton("Get the data");
        btnRefresh.addActionListener(this);

        // add the labels, text areas, and button to the panel
        JPanel panel = new JPanel();
        panel.setBackground(Color.decode("#011725")); // main panel colour
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(); // aligns to the centre
        c.insets = new Insets(10, 10, 10, 10);
        c.gridx = 0;
        c.gridy = 0;
        panel.add(lblRecentResults, c);
        c.gridx = 1;
        panel.add(spRecentResults, c);
        c.gridx = 0;
        c.gridy = 1;
        panel.add(lblAbnormalResults, c);
        c.gridx = 1;
        panel.add(spAbnormalResults, c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        panel.add(btnRefresh, c);
        add(panel, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1350, 900);  // set the size of the frame
        setLocationRelativeTo(null);  // centre the frame on the screen

    }

    //---------------------------------------------     BACK END     ---------------------------------------------\\

    /**
     * this method handles the event when the 'Get the data' button is clicked
     * the method sets the text in the 'Recent Results' text field to the results of the 'getRecentResults' method
     * the method sets the text in the 'Abnormal Results' text field to the abnormal results of the
     * 'getRecentResults' method
     * precondition: The 'Get the data' button must be clicked
     * postcondition: The text fields 'Recent Results' and 'Abnormal Results' will be updated with the corresponding
     * results from the 'getRecentResults' method
     */

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRefresh) {
            // get the recent results from the CSV file
            String[] results = getRecentResults();
            txtRecentResults.setText(results[0]);
            txtAbnormalResults.setText(results[1]);

            if (e.getSource() == btnRefresh) {

            }
        }
    }

    // -----------------------  reading the CSV file, making a hash map and for each loop ----------------------- \\

    /**
     * this method reads a CSV file containing patient data and stores the information in a HashMap
     * the method also checks for abnormal results and appends them to the 'abnormalResults' StringBuilder
     * the method also checks for recent results and appends them to the 'recentResults' StringBuilder
     * precondition: The file path passed to the method must be a valid and accessible CSV file containing patient data
     * postcondition: The method will return a String array containing the recent results and abnormal results
     * a string array containing the recent results and abnormal results
     */
    private String[] getRecentResults() {
        String file =
                "BloodTestsDataFinal.csv"; //
        // location of the CSV file
        BufferedReader reader = null;
        String line = "";

        StringBuilder recentResults = new StringBuilder();
        StringBuilder abnormalResults = new StringBuilder();

        HashMap<String, ArrayList<String>> dict = new HashMap<>();
        String[] headerValues = null;
        int lineNumber = 0;
        try {
            reader = new BufferedReader(new FileReader(file)); // a more efficient scanner
            while ((line = reader.readLine()) != null) {
                // Skip the first line (header row)
                if (lineNumber == 0) {
                    headerValues = line.split(",");
                    for (String hv : headerValues) { // enhanced loop OR for each loop which loops through the header
                        // values of the CSV file, reads the column, and stores them into array list
                        dict.put(hv, new ArrayList<>());
                    }
                    lineNumber++;
                    continue;

                }

                String[] values = line.split(",");

                for (int i = 0; i < headerValues.length; i++) {
                    String header = headerValues[i];
                    dict.get(header).add(values[i]);
                }
                lineNumber++;
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        lineNumber--;


        // ------------------------------  list of arrays to get using hash map  ------------------------------ \\

        ArrayList<String> firstName = dict.get("First Name"); // string
        ArrayList<String> lastName = dict.get("Last Name"); // string
        ArrayList<String> ageNumber = dict.get("Age"); // int
        ArrayList<String> fbsValCol = dict.get("FBS"); // double
        ArrayList<String> fbsHistCol = dict.get("FBS History"); // string
        ArrayList<String> hdlCol = dict.get("HDL");
        ArrayList<String> ldlCol = dict.get("LDL");
        ArrayList<String> a1c = dict.get("A1C");
        ArrayList<String> cortisolLevels = dict.get("Cortisol Levels");
        ArrayList<String> smokingHistory = dict.get("Smoking History"); // string
        ArrayList<String> medications = dict.get("Medications"); // string
        ArrayList<String> symptoms = dict.get("Symptoms"); // string
        ArrayList<String> hormoneDeficiency = dict.get("Hormone deficiency"); // string

        // adds the names of the patients to the dropdown menu on the left side of the application
        for (int i = 0; i < lineNumber; i++) {
            cbPatients.addItem(lastName.get(i) + ", " + firstName.get(i));
        }

        cbPatients.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                System.out.println(e.getItem());

            }
        });

        cbPatients.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = cbPatients.getSelectedIndex();
                // find the index of the name of the patient in the JTextArea
                String patientName = firstName.get(selectedIndex);
                int index = txtRecentResults.getText().indexOf(patientName);
                // move the cursor to the index of the patient name
                txtRecentResults.setCaretPosition(index);
            }
        });

        cbPatients.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = cbPatients.getSelectedIndex();
                // find the index of the name of the patient in the JTextArea
                String patientName = firstName.get(selectedIndex);
                int index = txtAbnormalResults.getText().indexOf(patientName);
                // move the cursor to the index of the patient name
                txtAbnormalResults.setCaretPosition(index);
            }
        });

        // a full list of the medications that can affect the FBS and cause abnormal results
        // In this case, patients are recommended to retake the test
        String[] medicationsThatAffectFBS = {"Caffeine", "Alcohol", "Betablockers", "Atenolol", "Bisoprolol", "Metoprolol", "Nadolol", "Propranolol", "Timolol", "Carvedilol", "Labetalol", "Esmolol", "Sotalol", "Nebivolol", "Acebutolol", "Pindolol", "Bupranolol", "Oxprenolol", "Antibiotics"};

        // a full list of the symptoms that should be examined to confirm "Hypoglycemia"
        // "Hypoglycemia" is a condition when the blood sugar levels are much lower than usual and should be addressed
        // immediately
        String[] symptomsThatDecreaseFBS = {"Fatigue", "Syncope", "Confusion", "Paleness", "Heart palpitation",
                "Hepatic",
                "Cardiac failure", "Sepsis", "Shakiness", "Sweating", "Dizziness"};

        // a full list of the hormone deficiencies that can decrease the FBS levels, but the patient might not
        // necessarily have
        // Hypoglycemia
        String[] hormonesThatDecreaseFBS = {"Cortisol", "Glucagon", "Epinephrine", "Hypothyroidism"};

        // a full list of the hormone deficiencies that can increase the FBS levels, but the patient might not be necessarily
        // diabetic
        String[] hormonesThatIncreaseFBS = {"Growth Hormone (GH)", "Hyperinsulinism", "Somatostatinoma", "Glucagonoma"};

        for (int i = 0; i < lineNumber; i++) {
            String firstnameDisplayed = firstName.get(i);
            String lastnameDisplayed = lastName.get(i);
            int ageNumberDisplayed = Integer.parseInt(ageNumber.get(i));
            double testValueFBS = Double.parseDouble(fbsValCol.get(i));
            String fbsHist = fbsHistCol.get(i);
            double testValueHDL = Double.parseDouble(hdlCol.get(i));
            double testValueLDL = Double.parseDouble(ldlCol.get(i));
            double a1cDisplayed = Double.parseDouble(a1c.get(i));
            double cortisolDisplayed = Double.parseDouble(cortisolLevels.get(i));
            String smokinghistoryDisplayed = smokingHistory.get(i);
            String medicationsDisplayed = medications.get(i);
            String symptomsDisplayed = symptoms.get(i);
            String hormoneDeficiencyDisplayed = hormoneDeficiency.get(i);

            recentResults.append("\n");
            recentResults.append(" --------------- ").append("Patient Number ").append(i + 1).append(" " +
                    "--------------- ").append(
                    "\n\n"); // the index for patient (which patient is shown)

            recentResults.append("Your name: ").append(firstnameDisplayed).append("\n");
            recentResults.append("Your last name: ").append(lastnameDisplayed).append("\n");
            recentResults.append("Your age: ").append(ageNumberDisplayed).append("\n");
            recentResults.append("FBS: ").append(testValueFBS).append("\n");
            recentResults.append("FBS History: ").append(fbsHist).append("\n");
            recentResults.append("HDL: ").append(testValueHDL).append("\n");
            recentResults.append("LDL: ").append(testValueLDL).append("\n");
            recentResults.append("A1C: ").append(a1cDisplayed).append("\n");
            recentResults.append("Cortisol Levels: ").append(cortisolDisplayed).append("\n");
            recentResults.append("Smoking History: ").append(smokinghistoryDisplayed).append("\n");
            recentResults.append("Medications: ").append(medicationsDisplayed).append("\n");
            recentResults.append("Symptoms: ").append(symptomsDisplayed).append("\n");
            recentResults.append("Hormone deficiencies: ").append(hormoneDeficiencyDisplayed).append("\n");

            // shows the first and last name of the patient in the results box
            abnormalResults.append("\n");
            abnormalResults.append(" --------------- ").append("Patient Number ").append(i + 1).append(" --------------- ").append(
                    "\n\n");
            abnormalResults.append("Patient: ").append(firstnameDisplayed).append(" ").append(lastnameDisplayed);
            abnormalResults.append("\n\n");

            final int ADULT_AGE_MINIMUM = 18;
            final int TEENAGE_AGE_MINIMUM = 12;
            final int TEENAGE_AGE_MAXIMUM = 17;
            final int CHILDREN_AGE_MINIMUM = 1;
            final int CHILDREN_AGE_MAXIMUM = 11;

            // ------------------------------  ADULTS  ------------------------------ \\

            if (ageNumberDisplayed >= ADULT_AGE_MINIMUM) {

                // start of examination for adults

                if (fbsHistCol.get(i).equals("Yes")) { // check if the patient has a history of having high BS
                    abnormalResults.append("\n");
                    abnormalResults.append("Since you also have a history for having high blood sugar, you are highly advised to talk to your doctor");
                    abnormalResults.append("\n");
                }

                if (a1cDisplayed > 6.4) { // if A1C is higher than the healthy range
                    abnormalResults.append("If your A1C (a measure of your average blood sugar levels over the past 2-3 months) is higher than normal, you should speak with your healthcare provider for guidance on how to lower it. They may recommend lifestyle changes such as eating a healthy diet, increasing physical activity, and losing weight if necessary. They may also prescribe medications if needed. It's important to follow their recommendations and to monitor your blood sugar levels regularly to ensure that they are returning to a healthy range.");
                    abnormalResults.append("\n");
                }

                if (a1cDisplayed < 4.5) {
                    abnormalResults.append("Your A1C is lower than the healthy range, it may indicate that your blood" +
                            " sugar levels are too low (hypoglycemia). This can be caused by a variety of factors, including taking too much insulin or other blood sugar-lowering medication, skipping meals, or exercising excessively without adjusting your medication or food intake.\n" +
                            "\n" +
                            "If you suspect that you have hypoglycemia, it's important to speak with your healthcare provider as soon as possible. They may adjust your medication or recommend changes to your diet or exercise routine to help bring your blood sugar levels back to the healthy range.\n" +
                            "\n" +
                            "It's important to check your blood sugar level frequently, especially if you are " +
                            "experiencing symptoms of hypoglycemia such as shakiness, sweating, dizziness, or confusion. You should also carry a source of quick-acting sugar, like glucose tablets or fruit juice, with you in case you need to treat an episode of hypoglycemia.");
                    abnormalResults.append("\n");
                }

                if (a1cDisplayed <= 6.4 && a1cDisplayed >= 4.5) { // check if A1C is in a healthy range

                    if (testValueFBS >= 70 && testValueFBS < 110) {   // check if the FBS value is within the range
                        // 70-110 which is considered to be normal and healthy AND A1C is in a healthy range
                        abnormalResults.append("Your FBS is all good! You also have a healthy A1C which means your " +
                                "blood sugar has stayed in a healthy range in the past 3 months. There is nothing to " +
                                "be worried about!");
                        abnormalResults.append("\n");
                    }

                    if (testValueFBS >= 110 & testValueFBS <= 120) { // check if the FBS value is borderline. In that
                        // case, it is advised for the patient to retake the test
                        abnormalResults.append("Your FBS is borderline. It is recommended for you to retake the test so " +
                                "that we can be fully sure about the validity of the results.").append("\n").append("\n");
                    }

                    for (String medicationThatAffectsFBS : medicationsThatAffectFBS) {
                        if (testValueFBS > 120 && medications.contains(medicationThatAffectsFBS)) {
                            // high FBS,
                            // but interfering factors in medications without having a history of high BS
                            abnormalResults.append("Your Fasting blood sugar is higher than normal.").append("\n").append("\n");
                            abnormalResults.append("This might be due to the fact that you had ").append(medicationsDisplayed).append(" this morning. " +
                                    "Since " +
                                    "your medical history shows that you haven't previously been diagnosed with high fasting " +
                                    "blood sugar, and that your A1C (median blood sugar in the past 3 months) falls within the " +
                                    "normal range, you are most likely healthy.").append("\n").append("\n");
                        }
                        break;
                    }

                    if (testValueFBS > 120) { // high FBS
                        abnormalResults.append("You have high fasting blood sugar (FBS) levels, but your A1C (a " +
                                "measure of your average blood sugar levels over the past 2-3 months) is normal, it may indicate that your blood sugar levels are elevated only during certain times of the day or that your blood sugar levels fluctuate frequently. This condition is called \"dawn phenomenon\" or \"somogyi effect\"\n" +
                                "\n" +
                                "It's important to speak with your healthcare provider to determine the cause of your high FBS levels and to develop a plan to manage them. They may recommend lifestyle changes such as eating a healthy diet, increasing physical activity, and losing weight if necessary. They may also adjust your medication or recommend changes to your diet or exercise routine to help bring your blood sugar levels back to the healthy range.\n" +
                                "\n" +
                                "It's also important to monitor your blood sugar levels regularly, including fasting blood sugar and post-meal blood sugar levels to get a better understanding of your glucose control throughout the day. Your healthcare provider will be able to use this information to make adjustments to your treatment plan if necessary.");
                        abnormalResults.append("\n");

                        if (cortisolDisplayed > 25) { // if high FBS & high Cortisol (high FBS could be a cause of high
                            // Cortisol)
                            abnormalResults.append("Having both high fasting blood sugar (FBS) and high cortisol levels could indicate a condition known as metabolic dysfunction or metabolic syndrome. Metabolic dysfunction is a cluster of conditions that occur together, increasing the risk of heart disease, diabetes, and stroke.\n" +
                                    "\n" +
                                    "High cortisol levels can contribute to high FBS by causing insulin resistance, which makes it difficult for the body to process sugar properly. High cortisol levels can also lead to weight gain, which is another risk factor for metabolic dysfunction.\n" +
                                    "\n" +
                                    "It's important to speak with your healthcare provider to determine the cause of your high FBS and cortisol levels and to develop a plan to manage them. They may recommend lifestyle changes such as eating a healthy diet, increasing physical activity, and losing weight if necessary. They may also prescribe medication if needed.\n" +
                                    "\n" +
                                    "It's also important to manage stress, try to get enough sleep, eat a healthy diet, and engage in regular physical activity, as these can help to decrease cortisol levels and improve glucose control.\n" +
                                    "\n" +
                                    "Your healthcare provider may also recommend additional tests like glucose tolerance test, insulin level test, etc. to get a better understanding of your metabolic dysfunction, and to make adjustments to your treatment plan if necessary.");
                            abnormalResults.append("\n");
                        }
                        for (String hormoneThatIncreaseFBS : hormonesThatIncreaseFBS) {
                            if (hormoneDeficiency.contains(hormoneThatIncreaseFBS)) { // check if high FBS is caused
                                // by a hormone deficiency
                                abnormalResults.append("You have high fasting blood sugar (FBS) levels and hormone " +
                                        "deficiencies that contribute to the high FBS, it could indicate a condition called secondary hyperglycemia. Secondary hyperglycemia is high blood sugar that is caused by an underlying medical condition or medication.\n" +
                                        "\n" +
                                        "Hormone deficiencies such as diabetes insipidus, which affects the body's ability to regulate blood sugar, or acromegaly, which is caused by excess growth hormone production, can lead to high blood sugar levels.\n" +
                                        "\n" +
                                        "It's important to speak with your healthcare provider to determine the cause" +
                                        " of your high FBS and hormone deficiencies and to develop a plan to manage them. They may recommend hormone replacement therapy to address the hormone deficiencies, and may also adjust your medication or recommend changes to your diet or exercise routine to help bring your blood sugar levels back to the healthy range.");
                                abnormalResults.append("\n");
                            }
                            break;
                        }
                        if (smokinghistoryDisplayed.equals("Yes")) { // check if high FBS is caused by smoking
                            abnormalResults.append("You have high fasting blood sugar (FBS) levels and you smoke, " +
                                    "it's likely that smoking is contributing to your high FBS levels. Smoking can increase insulin resistance, which makes it difficult for the body to process sugar properly, leading to high FBS levels and an increased risk of developing type 2 diabetes. Nicotine, the main addictive substance in cigarettes, can cause a temporary increase in blood glucose, which can lead to high FBS levels. Additionally, smoking can cause damage to the blood vessels that supply blood to the pancreas, which can affect the pancreas' ability to produce insulin, leading to high FBS levels.\n" +
                                    "\n" +
                                    "The most important step you can take to improve your FBS and overall health is to quit smoking. Quitting smoking can help to improve insulin sensitivity, decrease the risk of developing type 2 diabetes, and improve overall health. It's important to speak with your healthcare provider if you are considering quitting smoking, as they can help you find resources and support to help you quit.");
                            abnormalResults.append("\n");
                        }
                    }

                    if (testValueFBS < 70) { // low FBS
                        abnormalResults.append("Your fasting blood sugar (FBS) is lower than normal but your A1C (a " +
                                "measure of your average blood sugar levels over the past 2-3 months) is in the healthy range, it may indicate that your blood sugar levels are lower in the morning and then increase throughout the day. This could be caused by taking too much insulin or other blood sugar-lowering medication at night, or by not eating enough at night.\n" +
                                "\n" +
                                "It's important to speak with your healthcare provider to determine the cause of your low FBS levels and to develop a plan to manage them. They may adjust your medication or recommend changes to your diet or exercise routine to help bring your blood sugar levels back to the healthy range.\n" +
                                "\n" +
                                "It's also important to monitor your blood sugar levels regularly, including fasting blood sugar and post-meal blood sugar levels to get a better understanding of your glucose control throughout the day. Your healthcare provider will be able to use this information to make adjustments to your treatment plan if necessary.\n" +
                                "\n" +
                                "It's recommended to consume small snacks before bedtime to help prevent low blood sugar levels during the night, such as crackers with peanut butter or cheese, a small bowl of cereal or yogurt with fruit.");
                        abnormalResults.append("\n");

                        for (String hormoneThatDecreaseFBS : hormonesThatDecreaseFBS) { // check if low FBS is caused
                            // by a hormone deficiency
                            if (hormoneDeficiency.contains(hormoneThatDecreaseFBS)) {
                                abnormalResults.append("You have low fasting blood sugar (FBS) levels and hormone " +
                                        "deficiencies that contribute to the low FBS, it is important to address both issues.\n" +
                                        "\n" +
                                        "Hormone deficiencies such as hypothyroidism, can slow down metabolism, which can lead to low blood sugar levels.\n" +
                                        "\n" +
                                        "It's important to speak with your healthcare provider to determine the cause of your low FBS and hormone deficiencies and to develop a plan to manage them. They may recommend hormone replacement therapy to address the hormone deficiencies, and may also adjust your medication or recommend changes to your diet or exercise routine to help bring your blood sugar levels back to the healthy range.");
                                abnormalResults.append("\n");
                            }
                            break;
                        }

                        for (String symptomThatDecreaseFBS : symptomsThatDecreaseFBS) { // check if low FBS is caused
                            // by a hormone deficiency
                            if (hormoneDeficiency.contains(symptomThatDecreaseFBS)) {
                                abnormalResults.append("You have low fasting blood sugar (FBS) levels and symptoms " +
                                        "that may contribute to the low FBS, it could indicate a condition called hypoglycemia. Hypoglycemia is a condition where blood sugar levels drop below the normal range, which can cause symptoms such as shakiness, sweating, dizziness, confusion and others.\n" +
                                        "\n" +
                                        "There are several causes for hypoglycemia, including taking too much insulin or other blood sugar-lowering medications, skipping meals, or exercising excessively without adjusting your medication or food intake. Other causes may include certain medical conditions such as liver or kidney disease, certain medications, alcohol consumption and others.\n" +
                                        "\n" +
                                        "It's important to speak with your healthcare provider to determine the cause of your low FBS levels and to develop a plan to manage them. They may adjust your medication or recommend changes to your diet or exercise routine to help bring your blood sugar levels back to the healthy range.\n" +
                                        "\n" +
                                        "It's also important to monitor your blood sugar levels regularly, especially if you are experiencing symptoms of hypoglycemia such as shakiness, sweating, dizziness, or confusion. You should also carry a source of quick-acting sugar, like glucose tablets or fruit juice, with you in case you need to treat an episode of hypoglycemia.\n" +
                                        "\n" +
                                        "It's recommended to consume small snacks throughout the day, such as fruit, nuts, cheese, or crackers, to help keep your blood sugar levels stable and prevent hypoglycemia.\n" +
                                        "\n" +
                                        "Additionally, it's important to inform your healthcare provider of any symptoms you are experiencing and if you experience recurrent episodes of hypoglycemia. Your healthcare provider may recommend further testing and additional treatment to help you manage your condition.");
                                abnormalResults.append("\n");
                            }
                            break;
                        }
                    }

                    // end of examination for adults
                }
            }

            // ------------------------------  TEENAGERS  ------------------------------ \\

            if (ageNumberDisplayed >= TEENAGE_AGE_MINIMUM && ageNumberDisplayed <= TEENAGE_AGE_MAXIMUM) {

                // start of examination for teenagers

                if (fbsHistCol.get(i).equals("Yes")) { // check if the patient has a history of having high BS
                    abnormalResults.append("\n");
                    abnormalResults.append("Since you also have a history for having high blood sugar, you are highly advised to talk to your doctor");
                    abnormalResults.append("\n");
                }

                if (a1cDisplayed > 7.5) { // if A1C is higher than the healthy range
                    abnormalResults.append("If your A1C (a measure of your average blood sugar levels over the past 2-3 months) is higher than normal, you should speak with your healthcare provider for guidance on how to lower it. They may recommend lifestyle changes such as eating a healthy diet, increasing physical activity, and losing weight if necessary. They may also prescribe medications if needed. It's important to follow their recommendations and to monitor your blood sugar levels regularly to ensure that they are returning to a healthy range.");
                    abnormalResults.append("\n");
                }

                if (a1cDisplayed < 4.5) {
                    abnormalResults.append("Your A1C is lower than the healthy range, it may indicate that your blood" +
                            " sugar levels are too low (hypoglycemia). This can be caused by a variety of factors, including taking too much insulin or other blood sugar-lowering medication, skipping meals, or exercising excessively without adjusting your medication or food intake.\n" +
                            "\n" +
                            "If you suspect that you have hypoglycemia, it's important to speak with your healthcare provider as soon as possible. They may adjust your medication or recommend changes to your diet or exercise routine to help bring your blood sugar levels back to the healthy range.\n" +
                            "\n" +
                            "It's important to check your blood sugar level frequently, especially if you are " +
                            "experiencing symptoms of hypoglycemia such as shakiness, sweating, dizziness, or confusion. You should also carry a source of quick-acting sugar, like glucose tablets or fruit juice, with you in case you need to treat an episode of hypoglycemia.");
                    abnormalResults.append("\n");
                }

                if (a1cDisplayed <= 7.5 && a1cDisplayed >= 4.5) { // check if A1C is in a healthy range

                    if (testValueFBS >= 70 && testValueFBS < 140) {   // check if the FBS value is within the range
                        // 70-110 which is considered to be normal and healthy AND A1C is in a healthy range
                        abnormalResults.append("Your FBS is all good! You also have a healthy A1C which means your " +
                                "blood sugar has stayed in a healthy range in the past 3 months. There is nothing to " +
                                "be worried about!");
                        abnormalResults.append("\n");
                    }

                    if (testValueFBS >= 140 & testValueFBS <= 150) { // check if the FBS value is borderline. In that
                        // case, it is advised for the patient to retake the test
                        abnormalResults.append("Your FBS is borderline. It is recommended for you to retake the test so " +
                                "that we can be fully sure about the validity of the results.").append("\n").append("\n");
                    }

                    for (String medicationThatAffectsFBS : medicationsThatAffectFBS) {
                        if (testValueFBS > 150 && medications.contains(medicationThatAffectsFBS)) {
                            // high FBS,
                            // but interfering factors in medications without having a history of high BS
                            abnormalResults.append("Your Fasting blood sugar is higher than normal.").append("\n").append("\n");
                            abnormalResults.append("This might be due to the fact that you had ").append(medicationsDisplayed).append(" this morning. " +
                                    "Since " +
                                    "your medical history shows that you haven't previously been diagnosed with high fasting " +
                                    "blood sugar, and that your A1C (median blood sugar in the past 3 months) falls within the " +
                                    "normal range, you are most likely healthy.").append("\n").append("\n");
                        }
                        break;
                    }

                    if (testValueFBS > 150) { // high FBS
                        abnormalResults.append("You have high fasting blood sugar (FBS) levels, but your A1C (a " +
                                "measure of your average blood sugar levels over the past 2-3 months) is normal, it may indicate that your blood sugar levels are elevated only during certain times of the day or that your blood sugar levels fluctuate frequently. This condition is called \"dawn phenomenon\" or \"somogyi effect\"\n" +
                                "\n" +
                                "It's important to speak with your healthcare provider to determine the cause of your high FBS levels and to develop a plan to manage them. They may recommend lifestyle changes such as eating a healthy diet, increasing physical activity, and losing weight if necessary. They may also adjust your medication or recommend changes to your diet or exercise routine to help bring your blood sugar levels back to the healthy range.\n" +
                                "\n" +
                                "It's also important to monitor your blood sugar levels regularly, including fasting blood sugar and post-meal blood sugar levels to get a better understanding of your glucose control throughout the day. Your healthcare provider will be able to use this information to make adjustments to your treatment plan if necessary.");
                        abnormalResults.append("\n");

                        if (cortisolDisplayed > 25) { // if high FBS & high Cortisol (high FBS could be a cause of high
                            // Cortisol)
                            abnormalResults.append("Having both high fasting blood sugar (FBS) and high cortisol levels could indicate a condition known as metabolic dysfunction or metabolic syndrome. Metabolic dysfunction is a cluster of conditions that occur together, increasing the risk of heart disease, diabetes, and stroke.\n" +
                                    "\n" +
                                    "High cortisol levels can contribute to high FBS by causing insulin resistance, which makes it difficult for the body to process sugar properly. High cortisol levels can also lead to weight gain, which is another risk factor for metabolic dysfunction.\n" +
                                    "\n" +
                                    "It's important to speak with your healthcare provider to determine the cause of your high FBS and cortisol levels and to develop a plan to manage them. They may recommend lifestyle changes such as eating a healthy diet, increasing physical activity, and losing weight if necessary. They may also prescribe medication if needed.\n" +
                                    "\n" +
                                    "It's also important to manage stress, try to get enough sleep, eat a healthy diet, and engage in regular physical activity, as these can help to decrease cortisol levels and improve glucose control.\n" +
                                    "\n" +
                                    "Your healthcare provider may also recommend additional tests like glucose tolerance test, insulin level test, etc. to get a better understanding of your metabolic dysfunction, and to make adjustments to your treatment plan if necessary.");
                            abnormalResults.append("\n");
                        }
                        for (String hormoneThatIncreaseFBS : hormonesThatIncreaseFBS) {
                            if (hormoneDeficiency.contains(hormoneThatIncreaseFBS)) { // check if high FBS is caused
                                // by a hormone deficiency
                                abnormalResults.append("You have high fasting blood sugar (FBS) levels and hormone " +
                                        "deficiencies that contribute to the high FBS, it could indicate a condition called secondary hyperglycemia. Secondary hyperglycemia is high blood sugar that is caused by an underlying medical condition or medication.\n" +
                                        "\n" +
                                        "Hormone deficiencies such as diabetes insipidus, which affects the body's ability to regulate blood sugar, or acromegaly, which is caused by excess growth hormone production, can lead to high blood sugar levels.\n" +
                                        "\n" +
                                        "It's important to speak with your healthcare provider to determine the cause" +
                                        " of your high FBS and hormone deficiencies and to develop a plan to manage them. They may recommend hormone replacement therapy to address the hormone deficiencies, and may also adjust your medication or recommend changes to your diet or exercise routine to help bring your blood sugar levels back to the healthy range.");
                                abnormalResults.append("\n");
                            }
                            break;
                        }
                        if (smokinghistoryDisplayed.equals("Yes")) { // check if high FBS is caused by smoking
                            abnormalResults.append("You have high fasting blood sugar (FBS) levels and you smoke, " +
                                    "it's likely that smoking is contributing to your high FBS levels. Smoking can increase insulin resistance, which makes it difficult for the body to process sugar properly, leading to high FBS levels and an increased risk of developing type 2 diabetes. Nicotine, the main addictive substance in cigarettes, can cause a temporary increase in blood glucose, which can lead to high FBS levels. Additionally, smoking can cause damage to the blood vessels that supply blood to the pancreas, which can affect the pancreas' ability to produce insulin, leading to high FBS levels.\n" +
                                    "\n" +
                                    "The most important step you can take to improve your FBS and overall health is to quit smoking. Quitting smoking can help to improve insulin sensitivity, decrease the risk of developing type 2 diabetes, and improve overall health. It's important to speak with your healthcare provider if you are considering quitting smoking, as they can help you find resources and support to help you quit.");
                            abnormalResults.append("\n");
                        }
                    }

                    if (testValueFBS < 70) { // low FBS
                        abnormalResults.append("Your fasting blood sugar (FBS) is lower than normal but your A1C (a " +
                                "measure of your average blood sugar levels over the past 2-3 months) is in the healthy range, it may indicate that your blood sugar levels are lower in the morning and then increase throughout the day. This could be caused by taking too much insulin or other blood sugar-lowering medication at night, or by not eating enough at night.\n" +
                                "\n" +
                                "It's important to speak with your healthcare provider to determine the cause of your low FBS levels and to develop a plan to manage them. They may adjust your medication or recommend changes to your diet or exercise routine to help bring your blood sugar levels back to the healthy range.\n" +
                                "\n" +
                                "It's also important to monitor your blood sugar levels regularly, including fasting blood sugar and post-meal blood sugar levels to get a better understanding of your glucose control throughout the day. Your healthcare provider will be able to use this information to make adjustments to your treatment plan if necessary.\n" +
                                "\n" +
                                "It's recommended to consume small snacks before bedtime to help prevent low blood sugar levels during the night, such as crackers with peanut butter or cheese, a small bowl of cereal or yogurt with fruit.");
                        abnormalResults.append("\n");

                        for (String hormoneThatDecreaseFBS : hormonesThatDecreaseFBS) { // check if low FBS is caused
                            // by a hormone deficiency
                            if (hormoneDeficiency.contains(hormoneThatDecreaseFBS)) {
                                abnormalResults.append("You have low fasting blood sugar (FBS) levels and hormone " +
                                        "deficiencies that contribute to the low FBS, it is important to address both issues.\n" +
                                        "\n" +
                                        "Hormone deficiencies such as hypothyroidism, can slow down metabolism, which can lead to low blood sugar levels.\n" +
                                        "\n" +
                                        "It's important to speak with your healthcare provider to determine the cause of your low FBS and hormone deficiencies and to develop a plan to manage them. They may recommend hormone replacement therapy to address the hormone deficiencies, and may also adjust your medication or recommend changes to your diet or exercise routine to help bring your blood sugar levels back to the healthy range.");
                                abnormalResults.append("\n");
                            }
                            break;
                        }

                        for (String symptomThatDecreaseFBS : symptomsThatDecreaseFBS) { // check if low FBS is caused
                            // by a hormone deficiency
                            if (hormoneDeficiency.contains(symptomThatDecreaseFBS)) {
                                abnormalResults.append("You have low fasting blood sugar (FBS) levels and symptoms " +
                                        "that may contribute to the low FBS, it could indicate a condition called hypoglycemia. Hypoglycemia is a condition where blood sugar levels drop below the normal range, which can cause symptoms such as shakiness, sweating, dizziness, confusion and others.\n" +
                                        "\n" +
                                        "There are several causes for hypoglycemia, including taking too much insulin or other blood sugar-lowering medications, skipping meals, or exercising excessively without adjusting your medication or food intake. Other causes may include certain medical conditions such as liver or kidney disease, certain medications, alcohol consumption and others.\n" +
                                        "\n" +
                                        "It's important to speak with your healthcare provider to determine the cause of your low FBS levels and to develop a plan to manage them. They may adjust your medication or recommend changes to your diet or exercise routine to help bring your blood sugar levels back to the healthy range.\n" +
                                        "\n" +
                                        "It's also important to monitor your blood sugar levels regularly, especially if you are experiencing symptoms of hypoglycemia such as shakiness, sweating, dizziness, or confusion. You should also carry a source of quick-acting sugar, like glucose tablets or fruit juice, with you in case you need to treat an episode of hypoglycemia.\n" +
                                        "\n" +
                                        "It's recommended to consume small snacks throughout the day, such as fruit, nuts, cheese, or crackers, to help keep your blood sugar levels stable and prevent hypoglycemia.\n" +
                                        "\n" +
                                        "Additionally, it's important to inform your healthcare provider of any symptoms you are experiencing and if you experience recurrent episodes of hypoglycemia. Your healthcare provider may recommend further testing and additional treatment to help you manage your condition.");
                                abnormalResults.append("\n");
                            }
                            break;
                        }
                    }

                    // end of examination for teenagers
                }
            }

            // ------------------------------  CHILDREN  ------------------------------ \\

            if (ageNumberDisplayed >= CHILDREN_AGE_MINIMUM && ageNumberDisplayed <= CHILDREN_AGE_MAXIMUM) {

                // start of examination for children

                if (fbsHistCol.get(i).equals("Yes")) { // check if the patient has a history of having high BS
                    abnormalResults.append("\n");
                    abnormalResults.append("Since you also have a history for having high blood sugar, you are highly advised to talk to your doctor");
                    abnormalResults.append("\n");
                }

                if (a1cDisplayed > 8.0) { // if A1C is higher than the healthy range
                    abnormalResults.append("If your A1C (a measure of your average blood sugar levels over the past 2-3 months) is higher than normal, you should speak with your healthcare provider for guidance on how to lower it. They may recommend lifestyle changes such as eating a healthy diet, increasing physical activity, and losing weight if necessary. They may also prescribe medications if needed. It's important to follow their recommendations and to monitor your blood sugar levels regularly to ensure that they are returning to a healthy range.");
                    abnormalResults.append("\n");
                }

                if (a1cDisplayed < 4.5) {
                    abnormalResults.append("Your A1C is lower than the healthy range, it may indicate that your blood" +
                            " sugar levels are too low (hypoglycemia). This can be caused by a variety of factors, including taking too much insulin or other blood sugar-lowering medication, skipping meals, or exercising excessively without adjusting your medication or food intake.\n" +
                            "\n" +
                            "If you suspect that you have hypoglycemia, it's important to speak with your healthcare provider as soon as possible. They may adjust your medication or recommend changes to your diet or exercise routine to help bring your blood sugar levels back to the healthy range.\n" +
                            "\n" +
                            "It's important to check your blood sugar level frequently, especially if you are " +
                            "experiencing symptoms of hypoglycemia such as shakiness, sweating, dizziness, or confusion. You should also carry a source of quick-acting sugar, like glucose tablets or fruit juice, with you in case you need to treat an episode of hypoglycemia.");
                    abnormalResults.append("\n");
                }

                if (a1cDisplayed <= 8.0 && a1cDisplayed >= 4.5) { // check if A1C is in a healthy range

                    if (testValueFBS >= 80 && testValueFBS < 180) {   // check if the FBS value is within the range
                        // 70-110 which is considered to be normal and healthy AND A1C is in a healthy range
                        abnormalResults.append("Your FBS is all good! You also have a healthy A1C which means your " +
                                "blood sugar has stayed in a healthy range in the past 3 months. There is nothing to " +
                                "be worried about!");
                        abnormalResults.append("\n");
                    }

                    if (testValueFBS >= 180 & testValueFBS <= 190) { // check if the FBS value is borderline. In that
                        // case, it is advised for the patient to retake the test
                        abnormalResults.append("Your FBS is borderline. It is recommended for you to retake the test so " +
                                "that we can be fully sure about the validity of the results.").append("\n").append("\n");
                    }

                    for (String medicationThatAffectsFBS : medicationsThatAffectFBS) {
                        if (testValueFBS > 190 && medications.contains(medicationThatAffectsFBS)) {
                            // high FBS,
                            // but interfering factors in medications without having a history of high BS
                            abnormalResults.append("Your Fasting blood sugar is higher than normal.").append("\n").append("\n");
                            abnormalResults.append("This might be due to the fact that you had ").append(medicationsDisplayed).append(" this morning. " +
                                    "Since " +
                                    "your medical history shows that you haven't previously been diagnosed with high fasting " +
                                    "blood sugar, and that your A1C (median blood sugar in the past 3 months) falls within the " +
                                    "normal range, you are most likely healthy.").append("\n").append("\n");
                        }
                        break;
                    }

                    if (testValueFBS > 190) { // high FBS
                        abnormalResults.append("You have high fasting blood sugar (FBS) levels, but your A1C (a " +
                                "measure of your average blood sugar levels over the past 2-3 months) is normal, it may indicate that your blood sugar levels are elevated only during certain times of the day or that your blood sugar levels fluctuate frequently. This condition is called \"dawn phenomenon\" or \"somogyi effect\"\n" +
                                "\n" +
                                "It's important to speak with your healthcare provider to determine the cause of your high FBS levels and to develop a plan to manage them. They may recommend lifestyle changes such as eating a healthy diet, increasing physical activity, and losing weight if necessary. They may also adjust your medication or recommend changes to your diet or exercise routine to help bring your blood sugar levels back to the healthy range.\n" +
                                "\n" +
                                "It's also important to monitor your blood sugar levels regularly, including fasting blood sugar and post-meal blood sugar levels to get a better understanding of your glucose control throughout the day. Your healthcare provider will be able to use this information to make adjustments to your treatment plan if necessary.");
                        abnormalResults.append("\n");

                        if (cortisolDisplayed > 25) { // if high FBS & high Cortisol (high FBS could be a cause of high
                            // Cortisol)
                            abnormalResults.append("Having both high fasting blood sugar (FBS) and high cortisol levels could indicate a condition known as metabolic dysfunction or metabolic syndrome. Metabolic dysfunction is a cluster of conditions that occur together, increasing the risk of heart disease, diabetes, and stroke.\n" +
                                    "\n" +
                                    "High cortisol levels can contribute to high FBS by causing insulin resistance, which makes it difficult for the body to process sugar properly. High cortisol levels can also lead to weight gain, which is another risk factor for metabolic dysfunction.\n" +
                                    "\n" +
                                    "It's important to speak with your healthcare provider to determine the cause of your high FBS and cortisol levels and to develop a plan to manage them. They may recommend lifestyle changes such as eating a healthy diet, increasing physical activity, and losing weight if necessary. They may also prescribe medication if needed.\n" +
                                    "\n" +
                                    "It's also important to manage stress, try to get enough sleep, eat a healthy diet, and engage in regular physical activity, as these can help to decrease cortisol levels and improve glucose control.\n" +
                                    "\n" +
                                    "Your healthcare provider may also recommend additional tests like glucose tolerance test, insulin level test, etc. to get a better understanding of your metabolic dysfunction, and to make adjustments to your treatment plan if necessary.");
                            abnormalResults.append("\n");
                        }
                        for (String hormoneThatIncreaseFBS : hormonesThatIncreaseFBS) {
                            if (hormoneDeficiency.contains(hormoneThatIncreaseFBS)) { // check if high FBS is caused
                                // by a hormone deficiency
                                abnormalResults.append("You have high fasting blood sugar (FBS) levels and hormone " +
                                        "deficiencies that contribute to the high FBS, it could indicate a condition called secondary hyperglycemia. Secondary hyperglycemia is high blood sugar that is caused by an underlying medical condition or medication.\n" +
                                        "\n" +
                                        "Hormone deficiencies such as diabetes insipidus, which affects the body's ability to regulate blood sugar, or acromegaly, which is caused by excess growth hormone production, can lead to high blood sugar levels.\n" +
                                        "\n" +
                                        "It's important to speak with your healthcare provider to determine the cause" +
                                        " of your high FBS and hormone deficiencies and to develop a plan to manage them. They may recommend hormone replacement therapy to address the hormone deficiencies, and may also adjust your medication or recommend changes to your diet or exercise routine to help bring your blood sugar levels back to the healthy range.");
                                abnormalResults.append("\n");
                            }
                            break;
                        }
                        if (smokinghistoryDisplayed.equals("Yes")) { // check if high FBS is caused by smoking
                            abnormalResults.append("You have high fasting blood sugar (FBS) levels and you smoke, " +
                                    "it's likely that smoking is contributing to your high FBS levels. Smoking can increase insulin resistance, which makes it difficult for the body to process sugar properly, leading to high FBS levels and an increased risk of developing type 2 diabetes. Nicotine, the main addictive substance in cigarettes, can cause a temporary increase in blood glucose, which can lead to high FBS levels. Additionally, smoking can cause damage to the blood vessels that supply blood to the pancreas, which can affect the pancreas' ability to produce insulin, leading to high FBS levels.\n" +
                                    "\n" +
                                    "The most important step you can take to improve your FBS and overall health is to quit smoking. Quitting smoking can help to improve insulin sensitivity, decrease the risk of developing type 2 diabetes, and improve overall health. It's important to speak with your healthcare provider if you are considering quitting smoking, as they can help you find resources and support to help you quit.");
                            abnormalResults.append("\n");
                        }
                    }

                    if (testValueFBS < 80) { // low FBS
                        abnormalResults.append("Your fasting blood sugar (FBS) is lower than normal but your A1C (a " +
                                "measure of your average blood sugar levels over the past 2-3 months) is in the healthy range, it may indicate that your blood sugar levels are lower in the morning and then increase throughout the day. This could be caused by taking too much insulin or other blood sugar-lowering medication at night, or by not eating enough at night.\n" +
                                "\n" +
                                "It's important to speak with your healthcare provider to determine the cause of your low FBS levels and to develop a plan to manage them. They may adjust your medication or recommend changes to your diet or exercise routine to help bring your blood sugar levels back to the healthy range.\n" +
                                "\n" +
                                "It's also important to monitor your blood sugar levels regularly, including fasting blood sugar and post-meal blood sugar levels to get a better understanding of your glucose control throughout the day. Your healthcare provider will be able to use this information to make adjustments to your treatment plan if necessary.\n" +
                                "\n" +
                                "It's recommended to consume small snacks before bedtime to help prevent low blood sugar levels during the night, such as crackers with peanut butter or cheese, a small bowl of cereal or yogurt with fruit.");
                        abnormalResults.append("\n");

                        for (String hormoneThatDecreaseFBS : hormonesThatDecreaseFBS) { // check if low FBS is caused
                            // by a hormone deficiency
                            if (hormoneDeficiency.contains(hormoneThatDecreaseFBS)) {
                                abnormalResults.append("You have low fasting blood sugar (FBS) levels and hormone " +
                                        "deficiencies that contribute to the low FBS, it is important to address both issues.\n" +
                                        "\n" +
                                        "Hormone deficiencies such as hypothyroidism, can slow down metabolism, which can lead to low blood sugar levels.\n" +
                                        "\n" +
                                        "It's important to speak with your healthcare provider to determine the cause of your low FBS and hormone deficiencies and to develop a plan to manage them. They may recommend hormone replacement therapy to address the hormone deficiencies, and may also adjust your medication or recommend changes to your diet or exercise routine to help bring your blood sugar levels back to the healthy range.");
                                abnormalResults.append("\n");
                            }
                            break;
                        }

                        for (String symptomThatDecreaseFBS : symptomsThatDecreaseFBS) { // check if low FBS is caused
                            // by a hormone deficiency
                            if (hormoneDeficiency.contains(symptomThatDecreaseFBS)) {
                                abnormalResults.append("You have low fasting blood sugar (FBS) levels and symptoms " +
                                        "that may contribute to the low FBS, it could indicate a condition called hypoglycemia. Hypoglycemia is a condition where blood sugar levels drop below the normal range, which can cause symptoms such as shakiness, sweating, dizziness, confusion and others.\n" +
                                        "\n" +
                                        "There are several causes for hypoglycemia, including taking too much insulin or other blood sugar-lowering medications, skipping meals, or exercising excessively without adjusting your medication or food intake. Other causes may include certain medical conditions such as liver or kidney disease, certain medications, alcohol consumption and others.\n" +
                                        "\n" +
                                        "It's important to speak with your healthcare provider to determine the cause of your low FBS levels and to develop a plan to manage them. They may adjust your medication or recommend changes to your diet or exercise routine to help bring your blood sugar levels back to the healthy range.\n" +
                                        "\n" +
                                        "It's also important to monitor your blood sugar levels regularly, especially if you are experiencing symptoms of hypoglycemia such as shakiness, sweating, dizziness, or confusion. You should also carry a source of quick-acting sugar, like glucose tablets or fruit juice, with you in case you need to treat an episode of hypoglycemia.\n" +
                                        "\n" +
                                        "It's recommended to consume small snacks throughout the day, such as fruit, nuts, cheese, or crackers, to help keep your blood sugar levels stable and prevent hypoglycemia.\n" +
                                        "\n" +
                                        "Additionally, it's important to inform your healthcare provider of any symptoms you are experiencing and if you experience recurrent episodes of hypoglycemia. Your healthcare provider may recommend further testing and additional treatment to help you manage your condition.");
                                abnormalResults.append("\n");
                            }
                            break;
                        }
                    }

                    // end of examination for children
                }
            }
        }
        repaint();

        // final thank yous :)
        abnormalResults.append("\n\n");
        abnormalResults.append("-------------------------------------------------");
        abnormalResults.append("\n\n");
        abnormalResults.append("Thank you for using our self blood test analyzer :) \n" +
                "\n" +
                "It is our hope that you found it useful \n" +
                "\n" +
                "Knowing your blood test results can be incredibly useful for monitoring your health and being proactive about your well-being. We are glad that our self-blood test analyzer was able to provide you with the information you needed. In future versions we hope to add other blood tests, such as liver, kidney, and thyroid tests, to provide a more comprehensive view of your health. \n" +
                "\n" +
                "I am Kyle Sotoudeh, the creator, and I am glad it was able to provide you with the information you needed. We are continuously striving to make our analyzer even better and more comprehensive, so be sure to keep an eye out for future versions. :)");
        abnormalResults.append("\n\n");

        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));

        // Return the recent and abnormal results as an array
        return new String[]{recentResults.toString(), abnormalResults.toString()};
    }
}