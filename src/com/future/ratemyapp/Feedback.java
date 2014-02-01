package com.future.ratemyapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import com.future.ratemyapp.helpers.FeedbackHelper;
import com.future.ratemyapp.utils.Properties;

public class Feedback {

	
	public static String RatingTitle;
	public static String RatingMessage1;
	public static String RatingMessage2;
	public static String RatingYes;
	public static String RatingNo;
	public static String FeedbackTitle;
	public static String FeedbackMessage1;
	public static String FeedbackYes;
	public static String FeedbackNo;
	public static String FeedbackSubject;
	public static String FeedbackBody;
	
	public static String FeedbackTo;
	public static String CompanyName;
	public static String ApplicationName;
	public static int FirstCount = 1;
	public static int SecondCount = 3;
	public static boolean CountDays = false;
	
	
	private MIDlet midlet;
	private String contentID;
	private String Title;
	private String Message;
	private String YesText;
	private String NoText;
	private CommandListener listener;
	private Displayable previousScreen;
	
	
	static {
		Properties defaultsCultureLanguages = new Properties();
		InputStream input = Class.class.getResourceAsStream("/ratemyapp_def.properties");
		try
		{
			defaultsCultureLanguages.load(input, "UTF-8");
		} catch (Exception e1)
		{}
		try
		{
			input.close();
		} catch (Exception e1)
		{}
		String locale = System.getProperty("microedition.locale");
		if (locale == null)
			locale = "en-us"; //default
		locale = locale.toLowerCase();// to not have to deal with upper/lower case of locale
		Properties p = null;
		InputStream is = null;
		try
		{
			p = new Properties();
			is = Class.class.getResourceAsStream("/ratemyapp_"+locale+".properties");
			p.load(is, "UTF-8");
		} catch (Exception e)
		{
			try {
			if (locale.length()>2)
			{
				p = new Properties();
				is = Class.class.getResourceAsStream("/ratemyapp_"+locale.substring(0, 2)+".properties");
				p.load(is, "UTF-8");
			}
			else
			{
				p = new Properties();
				is = Class.class.getResourceAsStream("/ratemyapp_"+locale+"-"+defaultsCultureLanguages.getProperty(locale).toLowerCase()+".properties");
				p.load(is, "UTF-8");
			}
			}
			catch (Exception e2) {}
		}
		finally {
			if (is != null)
				try
				{
					is.close();
				} catch (IOException e)
				{}
		}
		RatingTitle = stringReplace(p.getProperty("RatingTitle"),"\\n","\n");;
		RatingMessage1 = stringReplace(p.getProperty("RatingMessage1"),"\\n","\n");;
		RatingMessage2 = stringReplace(p.getProperty("RatingMessage2"),"\\n","\n");;
		RatingYes = stringReplace(p.getProperty("RatingYes"),"\\n","\n");;
		RatingNo = stringReplace(p.getProperty("RatingNo"),"\\n","\n");;
		FeedbackTitle = stringReplace(p.getProperty("FeedbackTitle"),"\\n","\n");;
		FeedbackMessage1 = stringReplace(p.getProperty("FeedbackMessage1"),"\\n","\n");;
		FeedbackYes = stringReplace(p.getProperty("FeedbackYes"),"\\n","\n");;
		FeedbackNo = stringReplace(p.getProperty("FeedbackNo"),"\\n","\n");;
		FeedbackSubject = stringReplace(p.getProperty("FeedbackSubject"),"\\n","\n");;
		FeedbackBody = stringReplace(p.getProperty("FeedbackBody"),"\\n","\n");;
		defaultsCultureLanguages = null;
		p = null;
		is = null;
	}
	
    private Feedback(MIDlet midlet, String contentID)
    {
    	this.midlet = midlet;
    	this.contentID = contentID;
    	new Thread() {
    		public void run() {
    			try
				{
					Thread.sleep(3000);
				} catch (InterruptedException e)
				{}
    			FeedbackOverlayLoaded();
    		}
    	}.start();
    }
    
    public static void start(MIDlet midlet, String contentID)
    {
    	new Feedback(midlet, contentID);
    }

    /**
     * Reset review and feedback funtionality. Makes notifications active
     * again, for example, after a major application update.
     */
    public static void Reset()
    {
        FeedbackHelper.INSTANCE.Reset();
    }

    /**
     * Reset review and feedback funtionality. Makes notifications active
     * again, for example, after a major application update.
     */
    private void FeedbackOverlayLoaded()
    {
        // FeedbackTo property is mandatory and must be defined in xaml.
        /*if (GetFeedbackTo(this) == null || GetFeedbackTo(this).Length <= 0)
        {
            throw new ArgumentNullException("FeedbackTo", "Mandatory property not defined in FeedbackOverlay.");
        }*/

        /*// Application language override.
        if (GetLanguageOverride(this) != null)
        {
            OverrideLanguage();
        }*/


        // Set up FeedbackHelper with properties.
        FeedbackHelper.INSTANCE.setFirstCount(FirstCount);
        FeedbackHelper.INSTANCE.setSecondCount(SecondCount);
        FeedbackHelper.INSTANCE.setCountDays(CountDays);


        // Inform FeedbackHelper of the creation of this control.
        FeedbackHelper.INSTANCE.Launching();



        // Check if review/feedback notification should be shown.
        if (FeedbackHelper.INSTANCE.getState() == FeedbackHelper.FeedbackStateFirstReview)
        {
            SetupFirstMessage();

            showDialog(true);
        }
        else if (FeedbackHelper.INSTANCE.getState() == FeedbackHelper.FeedbackStateSecondReview)
        {
            SetupSecondMessage();
            showDialog(true);
        }
        else
        {
            FeedbackHelper.INSTANCE.setState(FeedbackHelper.FeedbackStateInactive);
        }
    }
    
    private void clearDialog()
    {
    	/*Display.getDisplay(midlet).callSerially(new Runnable() {
			public void run()
			{
				
			}
		});*/
    	Display.getDisplay(midlet).setCurrent(previousScreen);
    }
    
    private void showDialog(boolean savePreviousScreen)
    {
    	if (savePreviousScreen)
    		previousScreen = Display.getDisplay(midlet).getCurrent();
    	Alert msgBox = new Alert(Title, Message, null, AlertType.CONFIRMATION);
    	msgBox.addCommand(new Command(YesText, Command.OK, 1));
    	msgBox.addCommand(new Command(NoText, Command.CANCEL, 2));
    	msgBox.setCommandListener(listener);
    	Display.getDisplay(midlet).setCurrent(msgBox);
    	/*Display.getDisplay(midlet).callSerially(new Runnable() {
			public void run()
			{
		    	
			}
		});*/
    }

    /**
     * Set up first review message shown after FirstCount launches.
     */
    private void SetupFirstMessage()
    {
        Title = stringReplace(RatingTitle, "{0}",GetApplicationName());
        Message = RatingMessage1;
        YesText = RatingYes;
        NoText = RatingNo;
        listener = new CommandListener() {
			public void commandAction(Command c, Displayable d)
			{
				clearDialog();
				if (c.getLabel().equals(YesText))
				{
					if (FeedbackHelper.INSTANCE.getState() == FeedbackHelper.FeedbackStateFirstReview)
			        {
			            Review();
			        }
					FeedbackHelper.INSTANCE.setState(FeedbackHelper.FeedbackStateInactive);
				}
				else
				{
					ShowFeedback();
				}
			}
        };
    }

    /**
     * Set up second review message shown after SecondCount launches.
     */
    private void SetupSecondMessage()
    {
        Title = stringReplace(RatingTitle, "{0}", GetApplicationName());
        Message = RatingMessage2;
        YesText = RatingYes;
        NoText = RatingNo;
        listener = new CommandListener() {
			public void commandAction(Command c, Displayable d)
			{
				clearDialog();
				if (c.getLabel().equals(YesText))
				{
					if (FeedbackHelper.INSTANCE.getState() == FeedbackHelper.FeedbackStateSecondReview)
			        {
			            Review();
			        }
					FeedbackHelper.INSTANCE.setState(FeedbackHelper.FeedbackStateInactive);
				}
				else
				{
					ShowFeedback();
				}
			}
        };
    }


    /**
     * Set up feedback message shown after first review message.
     */
    private void SetupFeedbackMessage()
    {
        Title = FeedbackTitle;
        Message = stringReplace(FeedbackMessage1, "{0}", GetApplicationName());
        YesText = FeedbackYes;
        NoText = FeedbackNo;
        listener = new CommandListener() {
			public void commandAction(Command c, Displayable d)
			{
				clearDialog();
				if (c.getLabel().equals(YesText))
				{
					if (FeedbackHelper.INSTANCE.getState() == FeedbackHelper.FeedbackStateFeedback)
			        {
						sendFeedback();
			        }
			        FeedbackHelper.INSTANCE.setState(FeedbackHelper.FeedbackStateInactive);
				}
				else
				{
					
				}
			}
        };
    }

    /**
     * Show feedback message.
     */
    private void ShowFeedback()
    {
    	if (Feedback.FeedbackTo == null || Feedback.FeedbackTo.equals("")) //No feedback email was specified
    		return;
        // Feedback message is shown only after first review message.
        if (FeedbackHelper.INSTANCE.getState() == FeedbackHelper.FeedbackStateFirstReview)
        {
            this.SetupFeedbackMessage();
            FeedbackHelper.INSTANCE.setState(FeedbackHelper.FeedbackStateFeedback);
            showDialog(false);
        }
        else
        {
            clearDialog();
            FeedbackHelper.INSTANCE.setState(FeedbackHelper.FeedbackStateInactive);
        }
    }

    /**
     * Launch appstore mobile link.
     */
    private void Review()
    {
        FeedbackHelper.INSTANCE.Reviewed();

        try {
        	midlet.platformRequest("http://store.ovi.mobi/content/"+contentID);
        } catch (Exception e) {} 
    }


    /**
     * Launch feedback email.
     */
    private void sendFeedback()
    {
        // Application version
        String version = getVersion();


        String company = getVendor();
        if (company == null || company.length() <= 0)
        {
            company = "<Company>";
        }


        // Body text including hardware, firmware and software info
        String body = stringReplace(FeedbackBody,"{0}" , "DeviceName");
        body = stringReplace(FeedbackBody,"{1}" , "DeviceManufacturer");
        body = stringReplace(FeedbackBody,"{2}" , "DeviceFirmwareVersion");
        body = stringReplace(FeedbackBody,"{3}" , "DeviceHardwareVersion");
        body = stringReplace(FeedbackBody,"{4}" , version);
        body = stringReplace(FeedbackBody,"{5}" , company);

        String subject = stringReplace(FeedbackSubject, "{0}", GetApplicationName());
        try
		{
			midlet.platformRequest("mailto:"+FeedbackTo+"?subject="+subject+ "&body="+body);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /// <summary>
    /// Override default assembly dependent localization for the control
    /// with another culture supported by the application and the library.
    /// </summary>
    /*private void OverrideLanguage()
    {
        CultureInfo originalCulture = Thread.CurrentThread.CurrentUICulture;
        CultureInfo newCulture = new CultureInfo(GetLanguageOverride(this));


        Thread.CurrentThread.CurrentCulture = newCulture;
        Thread.CurrentThread.CurrentUICulture = newCulture;


        SetFeedbackBody(this, AppResources.FeedbackBody);
        SetFeedbackMessage1(this, string.Format(AppResources.FeedbackMessage1, GetApplicationName()));
        SetFeedbackNo(this, AppResources.FeedbackNo);
        SetFeedbackSubject(this, string.Format(AppResources.FeedbackSubject, GetApplicationName()));
        SetFeedbackTitle(this, AppResources.FeedbackTitle);
        SetFeedbackYes(this, AppResources.FeedbackYes);
        SetRatingMessage1(this, AppResources.RatingMessage1);
        SetRatingMessage2(this, AppResources.RatingMessage2);
        SetRatingNo(this, AppResources.RatingNo);
        SetRatingTitle(this, string.Format(AppResources.RatingTitle, GetApplicationName()));
        SetRatingYes(this, AppResources.RatingYes);


        Thread.CurrentThread.CurrentCulture = originalCulture;
        Thread.CurrentThread.CurrentUICulture = originalCulture;
    }*/


    /**
     * Get application name.
     * @return Name of the application.
     */
    private String GetApplicationName()
    {
        String appName = ApplicationName;


        // If application name has not been defined by the application,
        // extract it from the JAD.
        if (appName == null || appName.length() <= 0)
        {
            appName = getAppName();
        }
        return appName;
    }
    
    /**
     * @return name of the MIDlet.
     */
    private String getAppName() {
        return midlet.getAppProperty("MIDlet-Name");
    }

    /**
     * @return vendor of the MIDlet.
     */
    private String getVendor() {
        return midlet.getAppProperty("MIDlet-Vendor");
    }

    /**
     * @return version of the MIDlet.
     */
    private String getVersion() {
        return midlet.getAppProperty("MIDlet-Version");
    }
    
    private static String stringReplace(String str, String pattern, String replace) 
    {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();

        while ( (e = str.indexOf( pattern, s ) ) >= 0 ) 
        {
            result.append(str.substring( s, e ) );
            result.append( replace );
            s = e+pattern.length();
        }
        result.append( str.substring( s ) );
        return result.toString();
    }
}
