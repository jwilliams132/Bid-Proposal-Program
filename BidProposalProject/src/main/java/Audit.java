import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Audit {
	private final String AUDITFILENAME = String.format("BidProposalProject\\src\\main\\resources\\Audit.txt");
//	private final String AUDITFILENAME = String.format("Audit %d-%d %d.%2d.%2d.txt",
//			LocalDateTime.now().getMonthValue(), LocalDateTime.now().getDayOfMonth(), LocalDateTime.now().getHour(),
//			LocalDateTime.now().getMinute(), LocalDateTime.now().getSecond());

	public Audit() {
		
	}

	public void add(String addition) {
	
		if(addition == "Start")
			addition = "\n##################################################\n"
					+ "# .###############################################\n"
					+ "#    %%%%%%%%%%%%%####%%%%%&%###%%%%%%%%%%%%%%%###\n"
					+ "#  *##/           ###/       ###%              ###\n"
					+ "#  *####(         ###        ###%              ###\n"
					+ "#  *#######       ##%         ##%              ###\n"
					+ "#   %#######%    ###.         %###            %###\n"
					+ "#    %########% ,###          .###           %####\n"
					+ "#    ,##########%%#/           ##%           #####\n"
					+ "#     ###########              (#/          .#####\n"
					+ "#     %##########  ##           #.          %#####\n"
					+ "#     ,##########,%####         %           ######\n"
					+ "#      ##########(#######                  .######\n"
					+ "#      %###################                #######\n"
					+ "#      .#####################.             #######\n"
					+ "#       ################.,#####,          .#######\n"
					+ "#       ################  #######*        %#######\n"
					+ "#       ,##############*  #########(      ########\n"
					+ "#        ##############    ###########   .########\n"
					+ "#        %#############    ############% %########\n"
					+ "#        .############     /############% ########\n"
					+ "#         ###########%      ############(   %#####\n"
					+ "#                                             %###\n"
					+ "#                                               %#";

		// Open the file for writing
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(AUDITFILENAME, true));
			// Write some text to the file
			writer.write(String.format("%d//%d  %02d:%02d:%02d : %s", LocalDateTime.now().getMonthValue(), LocalDateTime.now().getDayOfMonth(),LocalDateTime.now().getHour(),
					LocalDateTime.now().getMinute(), LocalDateTime.now().getSecond(), addition));
			writer.newLine(); // Add a new line
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}