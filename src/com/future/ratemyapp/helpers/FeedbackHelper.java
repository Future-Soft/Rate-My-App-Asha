package com.future.ratemyapp.helpers;

public class FeedbackHelper {

	public static final int FeedbackStateInactive		 = 0;
	public static final int FeedbackStateActive		 = 1;
	public static final int FeedbackStateFirstReview	 = 2;
	public static final int FeedbackStateSecondReview	 = 3;
	public static final int FeedbackStateFeedback		 = 4;


	
	
	// Constants
    private static final String LaunchCountKey = "RATE_MY_APP_LAUNCH_COUNT";
    private static final String ReviewedKey = "RATE_MY_APP_REVIEWED";
    private static final String LastLaunchDateKey = "RATE_MY_APP_LAST_LAUNCH_DATE";

 // Members
    private int firstCount;
    private int secondCount;
    private int state;
    private int launchCount = 0;
    public static final FeedbackHelper INSTANCE = new FeedbackHelper();
    private boolean reviewed = false;
    private long lastLaunchDate = System.currentTimeMillis();
    
    public boolean countDays;

    
    private FeedbackHelper()
    {
    	state = FeedbackStateActive;
    }
    
    /**
     * Called when FeedbackLayout control is instantiated, which is
 	 * supposed to happen when application's main page is instantiated.
     */
    public void Launching()
    {
        if (state == FeedbackStateActive)
        {
            LoadState();
        }
    }
    
    /**
     * Call when user has reviewed.
     */
    public void Reviewed()
    {
    	reviewed = true;
        StoreState();
    }
    
    /**
     * Reset review and feedback launch counter and review state.
     */
    public void Reset()
    {
    	launchCount = 0;
    	reviewed = false;
    	lastLaunchDate = System.currentTimeMillis();
        StoreState();
    }
    
    /**
     * Loads last state from storage and works out the new state.
     */
    private void LoadState()
    {
        try
        {
        	launchCount = Integer.parseInt(StorageHelper.getSetting(LaunchCountKey,"0"));
        	reviewed = Integer.parseInt(StorageHelper.getSetting(ReviewedKey,"0")) == 1;
        	lastLaunchDate = Long.parseLong(StorageHelper.getSetting(LastLaunchDateKey,"0"));

        	int timePart = 1000*60*60*24;
            if (!reviewed)
            {
                if (!countDays || lastLaunchDate/timePart < System.currentTimeMillis()/timePart)
                {
                	launchCount++;
                	lastLaunchDate = System.currentTimeMillis();
                }


                if (launchCount == firstCount)
                {
                    state = FeedbackStateFirstReview;
                }
                else if (launchCount == secondCount)
                {
                    state = FeedbackStateSecondReview;
                }


                StoreState();
            }
        }
        catch (Exception ex)
        {
            System.out.println("FeedbackHelper.LoadState - Failed to load state, Exception: "+ ex.toString());
        }
    }
    /**
     * Stores current state.
     */
    private void StoreState()
    {
        try
        {
            StorageHelper.storeSetting(LaunchCountKey, String.valueOf(launchCount), false);
            StorageHelper.storeSetting(ReviewedKey, reviewed?"1":"0", false);
            StorageHelper.storeSetting(LastLaunchDateKey, String.valueOf(lastLaunchDate), false);
            StorageHelper.flush();
        }
        catch (Exception ex)
        {
        	System.out.println("FeedbackHelper.StoreState - Failed to store state, Exception: "+ ex.toString());
        }
    }
    
    public void Review()
    {
        Reviewed();
        
    }

	/**
	 * @return the firstCount
	 */
	public int getFirstCount()
	{
		return firstCount;
	}

	/**
	 * @param firstCount the firstCount to set
	 */
	public void setFirstCount(int firstCount)
	{
		this.firstCount = firstCount;
	}

	/**
	 * @return the secondCount
	 */
	public int getSecondCount()
	{
		return secondCount;
	}

	/**
	 * @param secondCount the secondCount to set
	 */
	public void setSecondCount(int secondCount)
	{
		this.secondCount = secondCount;
	}

	/**
	 * @return the state
	 */
	public int getState()
	{
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(int state)
	{
		this.state = state;
	}

	/**
	 * @return the launchCount
	 */
	public int getLaunchCount()
	{
		return launchCount;
	}

	/**
	 * @param launchCount the launchCount to set
	 */
	public void setLaunchCount(int launchCount)
	{
		this.launchCount = launchCount;
	}

	/**
	 * @return the lastLaunchDate
	 */
	public long getLastLaunchDate()
	{
		return lastLaunchDate;
	}

	/**
	 * @param lastLaunchDate the lastLaunchDate to set
	 */
	public void setLastLaunchDate(long lastLaunchDate)
	{
		this.lastLaunchDate = lastLaunchDate;
	}

	/**
	 * @return the countDays
	 */
	public boolean isCountDays()
	{
		return countDays;
	}

	/**
	 * @param countDays the countDays to set
	 */
	public void setCountDays(boolean countDays)
	{
		this.countDays = countDays;
	}
}
