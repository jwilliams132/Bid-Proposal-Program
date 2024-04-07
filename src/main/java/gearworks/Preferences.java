package gearworks;

public class Preferences {

    public boolean upToMobsVisible;
    public boolean additionalMobsVisible;
	public boolean inputDirectoryUsed;

    public Themes theme;

    public int dropDeadPrice;
    public int standByPrice;

    public Preferences() {

        this.upToMobsVisible = true;
        this.additionalMobsVisible = true;
		this.inputDirectoryUsed = true;
        this.theme = Themes.DARK;
        this.dropDeadPrice = 0;
        this.standByPrice = 0;
    }

    public boolean isUpToMobsVisible() {

        return upToMobsVisible;
    }

    public void setUpToMobsVisible(boolean isUpToMobsVisible) {

        this.upToMobsVisible = isUpToMobsVisible;
    }

    public boolean isAdditionalMobsVisible() {

        return additionalMobsVisible;
    }

    public void setAdditionalMobsVisible(boolean isAdditionalMobsVisible) {

        this.additionalMobsVisible = isAdditionalMobsVisible;
    }

	public boolean isInputDirectoryUsed() {

		return inputDirectoryUsed;
	}

	public void setInputDirectoryUsed(boolean isInputDirectoryUsed) {
		
		this.inputDirectoryUsed = isInputDirectoryUsed;
	}

    public Themes getTheme() {

        return theme;
    }

    public void setTheme(Themes theme) {
        
        this.theme = theme;
    }

    public int getDropDeadPrice() {

        return dropDeadPrice;
    }

    public void setDropDeadPrice(int dropDeadPrice) {

        this.dropDeadPrice = dropDeadPrice;
    }

    public int getStandByPrice() {

        return standByPrice;
    }

    public void setStandByPrice(int standByPrice) {

        this.standByPrice = standByPrice;
    }
}
