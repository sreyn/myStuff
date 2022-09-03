//Java compilation command:
//First remove the line PACKAGE: STUFF from the file
//Then, type this in the directory in cmd:
//jar cmf timer.jar timer.mf *.class
//jar cvmf timer.jar timer.mf *.class makes it VERBOSE

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.*;
import java.util.Scanner;
import java.io.File;


import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.FloatControl;
import javax.swing.*;

import static javax.swing.SwingConstants.SOUTH;
import static javax.swing.SwingUtilities.getRootPane;


public class Driver {

    static JFrame frame;
    static JPanel panel;
    static JLabel totalTime;
    static JLabel perTime;
    static JLabel currentTime;
    static JLabel description;
    static JLabel dropDownTitle;
    static JLabel timePresetTitle;
    static JLabel alarmPresetTitle;
    static JLabel hourglass;

    static JLabel errorA;
    static JLabel errorB;

    static JTextField inpTime;
    static JTextField inpPer;

    static JButton pp;
    static JButton quit;
    static JButton dropDownEnter;

    static JComboBox<Integer> dropdownTime;
    static JComboBox<Integer> dropdownAlarm;

    static int defTimer = 5;
    static int defPer = 1;
    static int timeLeft = 0;

    static boolean timerActive = false;
    static boolean jj = false;
    static boolean waiter = true;

    static Instant start;

    //Staticing the driver
    static Driver player;
    //To store current position
    Long currentFrame;
    static Clip clip;
    //static Clip clYahoo;

    //current status of clip
    String stats;

    AudioInputStream ao, yh;
    static String filePath = "D://Downloads D/Censor Beep Sound Effect.wav";
    //static String fp = "D://Downloads D/YAHOO SOUND EFFECT (MARIO).wav";



    public static void main(String[] args)
    {
        try{
            player = new Driver();
            playerStopHandler(player);
            setVolume((float)0.03);

            //Frame setup
            frame = new JFrame();
            frame.setTitle("My Timer c:");
            frame.setPreferredSize(new Dimension(300, 310));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.addWindowListener(new java.awt.event.WindowAdapter()
            {
                public void windowClosed(java.awt.event.WindowEvent evt)
                {
                    System.exit(0);
                }
            }
            );

            //Panel Declaration
            panel = new JPanel();
            panel.setLayout(null);

            totalTime = new JLabel("How much total time: ");
            totalTime.setBounds(5,10, 140, 20);

            perTime = new JLabel("# of min. per alarm: ");
            perTime.setBounds(5, 35, 120, 20);

            currentTime = new JLabel("Time Left: " + timeLeft);
            currentTime.setBounds(5, 95, 150, 20);

            description = new JLabel("Ok! Total time: " + defTimer + " | minutes per alarm: " + defPer);
            description.setBounds(110, 65, 200, 20);
            description.setVisible(false);

            inpTime = new JTextField();
            inpTime.setBounds(130, 10, 90, 25);

            inpPer = new JTextField();
            inpPer.setBounds(130, 35, 90, 25);

            pp = new JButton();
            pp.setBounds(5, 65, 100, 20);
            pp.setText("Enter Input");

            frame.getRootPane().setDefaultButton(pp);
            //Here is the main stuff that's going to happen in the code.
            //Has a separate thread so as to not freeze the GUI in the middle of the program.

            pp.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if(timerActive == false)
                    {
                        new Thread()
                        {
                            @Override
                            public void run()
                            {
                                enterInputButtonHandler();
                            }
                        }.start();
                    }
                    else
                    {
                        System.out.println("Timer Active! Can't change shit!\n");
                    }
                }
            });

            quit = new JButton();
            quit.setBounds(150, 80, 80, 40);
            quit.setText("QUIT");

            quit.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    System.exit(0);
                    frame.dispose();

                }
            });

            Integer[] timePresets = new Integer[60];
            for(int i = 0; i < 60; ++i)
            {
                timePresets[i] = i + 1;
            }


            timePresetTitle = new JLabel("Minutes Total:");
            timePresetTitle.setBounds(5, 160, 200, 30);

            alarmPresetTitle = new JLabel("Minutes/Alarm:");
            alarmPresetTitle.setBounds(5, 190, 200, 30);

            dropdownTime = new JComboBox<>(timePresets);
            dropdownTime.setBounds(100, 160, 50, 30);

            dropdownAlarm = new JComboBox<>(timePresets);
            dropdownAlarm.setBounds(100, 190, 50, 30);

            dropDownTitle = new JLabel("Alternatively, choose a preset.");
            dropDownTitle.setBounds(5, 120, 200, 30);

            dropDownEnter = new JButton();
            dropDownEnter.setText("Input Presets");
            dropDownEnter.setBounds(5, 220, 230, 30);
            dropDownEnter.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(timerActive == false)
                    {
                        new Thread()
                        {
                            @Override
                            public void run()
                            {
                                dropDownHandler();
                            }
                        }.start();

                    }
                }
            });

            //Panel adding
            panel.add(totalTime);
            panel.add(inpTime);
            panel.add(perTime);
            panel.add(inpPer);
            panel.add(pp);
            panel.add(quit);
            panel.add(currentTime);
            panel.add(dropdownTime);
            panel.add(dropdownAlarm);
            panel.add(dropDownTitle);
            panel.add(dropDownEnter);
            panel.add(timePresetTitle);
            panel.add(alarmPresetTitle);

            //Frame finishing
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            //Done
            if(!frame.isDisplayable())System.out.println("done deal!");
        }
        catch(Exception e)
        {
            System.out.println("Generic exception caught, exiting program.");
            return;
        }
    }

    //Constructor to initialize streams and clip
    public Driver() throws UnsupportedAudioFileException, IOException, LineUnavailableException
    {
        ao = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
        clip = AudioSystem.getClip();

        //      yh = AudioSystem.getAudioInputStream(new File(fp).getAbsoluteFile());
        //      clYahoo = AudioSystem.getClip();

        clip.open(ao);
        clip.loop(Clip.LOOP_CONTINUOUSLY);

        //    clYahoo.open(yh);
        //  clYahoo.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public static void setVolume(float volume)
    {
        if(volume < 0f || volume > 1f)
        {
            throw new IllegalArgumentException("naw this shit's too different");
        }
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        // FloatControl gc = (FloatControl) clYahoo.getControl(FloatControl.Type.MASTER_GAIN);

        gainControl.setValue(20f * (float)Math.log10(volume));
        // gc.setValue(20f * (float)Math.log10(volume));

    }


    //Plays Audio
    public void play(Clip q) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        resetAudioStream();
        setVolume((float)0.03);
        q.start();
        stats = "play";
    }

    //Stops audio
    public void stop() throws UnsupportedAudioFileException,
            IOException, LineUnavailableException
    {
        currentFrame = 0L;
        clip.stop();
        clip.close();

       // clYahoo.stop();
       // clYahoo.close();
    }

    public void resetAudioStream() throws UnsupportedAudioFileException, IOException,
            LineUnavailableException
    {
        ao = AudioSystem.getAudioInputStream(
                new File(filePath).getAbsoluteFile());
    //    yh = AudioSystem.getAudioInputStream(
    //            new File(fp).getAbsoluteFile());

        clip.open(ao);
        setVolume((float)0.03);
      //  clYahoo.open(yh);

        clip.loop(Clip.LOOP_CONTINUOUSLY);
      //  clYahoo.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /***
     * calls Driver stop method, and handles the exceptions
     * Really just asks the music player to stop, checks for any possible errors.
     * @param d
     */
    public static void playerStopHandler(Driver d)
    {
        try{
            d.stop();
        }
        catch(UnsupportedAudioFileException c)
        {
            System.out.println("Unsupported Audio File Exception caught, exiting program.");
            return;
        }
        catch(IOException q)
        {
            System.out.println("IOException caught, exiting program.");
            return;
        }
        catch(LineUnavailableException L)
        {
            System.out.println("LineUnavailableException caught, exiting program.");
            return;
        }
    }

    /***
     * Calls the Driver play function, checks for errors.
     * d is used as "player", c is the clip,
     * And combined, just asks the system to play the clip, and checks for errors, like the clip being missing.
     * @param d
     * @param c
     */
    public static void playerPlayHandler(Driver d, Clip c)
    {
        try{
            d.play(c);
        }
        catch(UnsupportedAudioFileException g)
        {
            System.out.println("Unsupported Audio File Exception caught, exiting program.");
            return;
        }
        catch(IOException q)
        {
            System.out.println("IOException caught, exiting program.");
            return;
        }
        catch(LineUnavailableException L)
        {
            System.out.println("LineUnavailableException caught, exiting program.");
            return;
        }
    }

    /***
     * Condenses the code for the enter button when the input is typed.
     * Reads the input, and creates a timer using Instant's and Duration.between's
     */
    public static void enterInputButtonHandler()
    {
        boolean passed = true;
        defTimer = Integer.parseInt(inpTime.getText());
        defPer = Integer.parseInt(inpPer.getText());
        if(defPer <= 0)
        {
            JOptionPane.showMessageDialog(frame, "Please put an integer greater than 0 for the alarm.");
        }
        else
        {
            if(defPer > defTimer) defPer = defTimer;
            jj = true;
            timerActive = true;
            start = Instant.now();
            description.setText("Ok! Total time: " + defTimer +
                    " | minutes per alarm: " + defPer);
            description.setVisible(true);
            System.out.println("\nstarted timer! :)\n");
            timeLeft = defTimer - (int)Duration.between(start, Instant.now()).toMinutes();
            int secondsBetween = (int)Duration.between(start, Instant.now()).toSeconds() - (60 * timeLeft);
            String fullDisclosure = timeLeft + ":" + secondsBetween;
            currentTime.setText("Time Left: " + fullDisclosure);

            while (Duration.between(start, Instant.now()).toMinutes() < defTimer)
            {
                currentTime.setText("Time Left: " + fullDisclosure);
                Instant minuteTimer = Instant.now();
                while (Duration.between(minuteTimer, Instant.now()).toMinutes() < defPer && (Duration.between(start, Instant.now()).toMinutes() < defTimer))
                {
                    playerStopHandler(player);

                    if(Duration.between(start, Instant.now()).toMinutes() > defTimer)
                    {
                        break;
                    }
                }
                System.out.println(Duration.between(start, Instant.now()).toMinutes() +
                        " minutes down; Remaining Time: " +
                        (defTimer - Duration.between(start, Instant.now()).toMinutes()));
                timeLeft = defTimer - (int)Duration.between(start, Instant.now()).toMinutes();
                currentTime.setText("Time Left: " + fullDisclosure);
                playerPlayHandler(player, clip);
                Instant oneSec = Instant.now();
                while (Duration.between(oneSec, Instant.now()).toSeconds() < 2) {}
                playerStopHandler(player);
            }
            playerStopHandler(player);
            if(timeLeft == 0)
            {
                timerActive = false;
                JOptionPane.showMessageDialog(frame,"Timer finished!");
            }
        }

    }

    public static void dropDownHandler()
    {
        defTimer = (int)dropdownTime.getSelectedItem();
        defPer = (int)dropdownAlarm.getSelectedItem();
        if(defPer > defTimer) defPer = defTimer;
        jj = true;
        timerActive = true;
        start = Instant.now();
        description.setText("Ok! Total time: " + defTimer +
                " | minutes per alarm: " + defPer);
        description.setVisible(true);
        System.out.println("\nstarted timer! :)\n");
        timeLeft = defTimer - (int)Duration.between(start, Instant.now()).toMinutes();
        currentTime.setText("Time Left: " + timeLeft + " minutes");

        while (Duration.between(start, Instant.now()).toMinutes() < defTimer)
        {
            Instant minuteTimer = Instant.now();
            while (Duration.between(minuteTimer, Instant.now()).toMinutes() < defPer && (Duration.between(start, Instant.now()).toMinutes() < defTimer)) {
                playerStopHandler(player);

                if(Duration.between(start, Instant.now()).toMinutes() > defTimer)
                {
                    break;
                }
            }
            System.out.println(Duration.between(start, Instant.now()).toMinutes() +
                    " minutes down; Remaining Time: " +
                    (defTimer - Duration.between(start, Instant.now()).toMinutes()));
            timeLeft = defTimer - (int)Duration.between(start, Instant.now()).toMinutes();
            currentTime.setText("Time Left: " + timeLeft + " minutes");
            playerPlayHandler(player, clip);
            Instant oneSec = Instant.now();
            while (Duration.between(oneSec, Instant.now()).toSeconds() < 2) {}
            playerStopHandler(player);
        }
        playerStopHandler(player);
        if(timeLeft == 0)
        {
            timerActive = false;
            JOptionPane.showMessageDialog(frame,"Timer finished!");
        }
    }
}
