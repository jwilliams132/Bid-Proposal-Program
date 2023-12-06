package gearworks;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ContractorStorage {

    private FileManager fileManager = new FileManager();
    private Map<String, Contractor> contractorList = new TreeMap<String, Contractor>();
    private File contractorsFile;
    private String contractorFilePath = "src\\main\\resources\\gearworks\\Contractor List.csv";

    public ContractorStorage() {

        contractorsFile = fileManager.chooseFile(contractorFilePath, null,
                FileManager.fileChooserOptions.OPEN, null);

        ArrayList<String> fileContents = null;
        if (contractorsFile != null) {

            fileContents = fileManager.readFile(contractorsFile);
        }
        for (String string : fileContents) {

            String[] tokens = string.split("\\|");
            contractorList.put(tokens[0], new Contractor(tokens[0], tokens[1], tokens[2]));
        }
    }

    public void addToContractList(Contractor contractor) {

        contractorList.put(contractor.getContractorName(), contractor);
    }

    public void addToContractList(ArrayList<Contractor> contractors) {

        contractors.forEach(this::addToContractList);
    }

    public void formatContractorList() {

        List<String> contentToSave = contractorList.values().stream()
                .map(contractor -> contractor.getContractorName() + "|" +
                        contractor.getContractorPhoneNumber() + "|" +
                        contractor.getContractorEmail())
                .collect(Collectors.toList());

        fileManager.saveFile(contractorsFile, contentToSave);
    }

    public String getEmail(String contractorName) {

        return contractorList.containsKey(contractorName) ? contractorList.get(contractorName).getContractorEmail()
                : "==No Email Found==";
    }
}
