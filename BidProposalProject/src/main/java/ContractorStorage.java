import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.io.File;
import java.util.List;

public class ContractorStorage {

    private FileManager fileManager = new FileManager();
    private ArrayList<Contractor> contractorList = new ArrayList<Contractor>();
    private File contractorsFile;
    private String contractorFilePath = "BidProposalProject\\src\\main\\resources\\Contractor List.csv";

    public ContractorStorage() {

        contractorsFile = fileManager.chooseFile(contractorFilePath, null,
				FileManager.fileChooserOptions.OPEN, null);

        ArrayList<String> fileContents = null;
        if (contractorsFile != null) {
            
            fileContents = fileManager.readFile(contractorsFile);
        }
        for (String string : fileContents) {
            
            String[] tokens = string.split("\\|");
            contractorList.add(new Contractor(tokens[0], tokens[1], tokens[2]));
        }
    }

    public ArrayList<Contractor> getContractorList() {

        return contractorList;
    }

    public void addToContractList(Contractor contractor) {

        for (Contractor knownContractor : contractorList) {
            
            if (contractor.getContractorName().equals(knownContractor.getContractorName()))
                return;
        }
        contractorList.add(contractor);
        Collections.sort(contractorList);
    }

    public void addToContractList(ArrayList<Contractor> contractors) {
        
        contractors.forEach(this::addToContractList);
    }

    public void formatContractorList() {

        List<String> contentToSave = contractorList.stream()
                .map(contractor -> contractor.getContractorName() + "|" +
                                    contractor.getContractorPhoneNumber() + "|" +
                                    contractor.getContractorEmail())
                .collect(Collectors.toList());

        fileManager.saveFile(contractorsFile, contentToSave);
    }

    public String getEmail(String contractorName) {

        for (Contractor knownContractor : contractorList) {
            
            if (contractorName.equals(knownContractor.getContractorName())) {
                
                return knownContractor.getContractorEmail();
            }
        }
        return "=============No Email Found=============";
    }
}
