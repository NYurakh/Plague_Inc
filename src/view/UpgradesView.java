package view;


import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.Upgrade;

public class UpgradesView extends JDialog {
    private JList<String> upgradeList;
    private JButton buyButton;
    private DefaultListModel<String> listModel;
    private JLabel pointsLabel;

    private List<Upgrade> availableUpgrades;

    public UpgradesView(JFrame parent, List<Upgrade> upgrades) {
        super(parent, "Upgrades Store", true);
        this.availableUpgrades = upgrades;

        setSize(400, 300);
        setLocationRelativeTo(parent);

        pointsLabel = new JLabel("Points: 0");
        listModel = new DefaultListModel<>();
        for (Upgrade up : upgrades) {
            listModel.addElement(up.getName() + " (Cost: " + up.getCost() 
                  + ") -> " + up.getEffectDescription());
        }

        upgradeList = new JList<>(listModel);
        upgradeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(upgradeList);

        buyButton = new JButton("Buy Selected Upgrade");

        setLayout(new BorderLayout());
        add(pointsLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buyButton, BorderLayout.SOUTH);
    }

    public JButton getBuyButton() {
        return buyButton;
    }

    public void updatePointsDisplay(int points) {
        pointsLabel.setText("Points: " + points);
    }

    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public int getSelectedUpgradeIndex() {
        return upgradeList.getSelectedIndex();
    }

    public void removeUpgrade(int index) {
        listModel.remove(index);
    }

    public void updateAvailableUpgrades(List<Upgrade> upgrades) {
        listModel.clear();
        for (Upgrade up : upgrades) {
            listModel.addElement(up.getName() + " (Cost: " + up.getCost() + ") -> " + up.getEffectDescription());
        }
    }
   
    
}

