import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.*;
import java.util.Timer;
public class BTClicker implements ActionListener {
	//currentPrice = trenutna cena BTC na 1€112
	static double btc = 0, eur = 0, currentPrice = 0.00015, clickStrength = 0.000001, pcPower = 0;
	static int clickerLevel = 0, clickerUpgradeCost = 1, tiers = 0, pcTier = 0, s = 0;
	static JButton clicker, convert, upgradeClicker, pc1, surprise, invest;
	static JLabel currentBtc, currentEur, clickerUpgrade, pcStatus, btcPs, clickerStrength, eurDifference;
	static JFrame f;
	static boolean pcOnline = false;
	static int[] powerSupplied = new int[7];
	static double[] compPower = {0.000001, 0.000002, 0.000004, 0.000005, 0.000002, 0.000001, 0.000001};
	static Timer timer = new Timer();
	
	public static void main(String[] args) {
		f = new JFrame("BTClicker");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(null);
		
		Locale.setDefault(Locale.US);
		currentBtc = new JLabel(String.format("BTC: %.6f", btc));
		currentBtc.setFont(new Font(currentBtc.getName(), Font.PLAIN, 25));
		currentEur = new JLabel(String.format("EUR: %.2f€", eur));
		currentEur.setFont(new Font(currentBtc.getName(), Font.PLAIN, 25));
		
		clicker = new JButton("Mine BTC!");
		convert = new JButton("Transfer to EUR");
		clickerUpgrade = new JLabel("Clicker level: "+clickerLevel);
		upgradeClicker = new JButton("Level 1: "+clickerUpgradeCost+"€");
		pc1 = new JButton("PC");
		pcStatus = new JLabel("<html>PC Status: "+((pcOnline) ? "<font color=green>Online</font></html>" : "<font color=red>Offline</font></html>"));
		btcPs = new JLabel(String.format("BTC/s: %.6f", btcPS));
		clickerStrength = new JLabel("<html>"+String.format("Clicker strength: <br>BTC/pc: %.6f", clickStrength)+"</html>");
		surprise = new JButton("Surprise / Cost - 1BTC");
		invest = new JButton("Stock Market");
		eurDifference = new JLabel("0");
		eurDifference.setVisible(false);
		
		JButton[] buttons = {clicker, convert, upgradeClicker, pc1, surprise, invest};
		
		configureComponents(buttons, mainPanel);
		
		for(int i = 0; i < powerSupplied.length; i++) {
			powerSupplied[i] = 0;
		}
		
		//Nastavim pozicijo komponent
		clicker.setBounds(10, 60, 100, 30);
		convert.setBounds(120, 60, 130, 30);
		upgradeClicker.setBounds(10, 140, 130, 30);
		pc1.setBounds(170, 140, 70, 30);
		pcStatus.setBounds(170, 190, 120, 30);
		btcPs.setBounds(170, 220, 120, 30);
		clickerStrength.setBounds(10, 175, 150, 30);
		surprise.setBounds(300, 190, 180, 30);
		invest.setBounds(300, 140, 120, 30);
		eurDifference.setBounds(370, 50, 120, 20);
		clickerUpgrade.setBounds(10, 100, 280, 50);
		currentBtc.setBounds(10, 0, 250, 50);
		currentEur.setBounds(300, 0, 250, 50);
		
		//dodam jih na panel
		mainPanel.add(eurDifference);
		mainPanel.add(pcStatus);
		mainPanel.add(btcPs);
		mainPanel.add(clickerStrength);
		mainPanel.add(surprise);
		mainPanel.add(invest);
		mainPanel.add(clickerUpgrade);
		mainPanel.add(currentBtc);
		mainPanel.add(currentEur);
		
		upgradeClicker.setEnabled(false);

		f.add(mainPanel);
		f.setSize(510, 300);
		f.setVisible(true);
		f.setLocationRelativeTo(null);
		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if(eurDifference.isVisible()) {s++;}
				if(s == 2) {
					eurDifference.setVisible(false);
					s = 0;
				}			
			}
		}, 200, 1000);		
	}
	
	public static void configureComponents(JButton[] buttons, JPanel panel) {
		//Config
		BTClicker bc = new BTClicker();
		for(JButton c : buttons) {
			panel.add(c);
			c.setFocusable(false);
			c.addActionListener(bc);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		double oldEur = eur;
		if(e.getSource() == clicker) {
			btc += clickStrength;
		}else if(e.getSource() == convert) {
			eur += btc / currentPrice;
			btc = 0;
		}else if(e.getSource() == upgradeClicker) {
			if(eur >= clickerUpgradeCost && clickerLevel < 10) {
				eur -= clickerUpgradeCost;
				clickerLevel++;
				clickerUpgrade.setText("Clicker level: "+clickerLevel);
				clickStrength *= 2.3;
				clickerUpgradeCost *= 2.5;
				upgradeClicker.setText("Level "+(clickerLevel+1)+": "+clickerUpgradeCost+"€");
			}
			if(clickerLevel == 10) {
				upgradeClicker.setText("MAX Level");
			}
		}else if(e.getSource() == pc1) {
			generateParts(true);
			
		}else if(e.getSource() == surprise && btc >= 1) {
			JOptionPane.showMessageDialog(f, "You got scammed", "Scam", JOptionPane.INFORMATION_MESSAGE);
			btc -= 1;
			
		}else if(e.getSource() == invest) {
			if(pcTier >= 1) {
				stockMarket();
			} else {
				JOptionPane.showMessageDialog(f, "You need atleast PC Tier 1 to access stock market!", "Denied", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
		//PC
		if(partsCreated) {
			for(int i = 0; i < buttons.length; i++) {
				if(e.getSource() == buttons[i] && (eur >= prices[i])) {
					eur -= prices[i];
					tier[i]++;
					powerSupplied[i]++;
					compPower[i] *= powerSupplied[i];
					pcPower += compPower[i];
					if(tier[i] == maxTier[i]) {
						buttons[i].setEnabled(false);
					}else {
						prices[i] *= 2;
					}
				}
				if(tier[i] >= 1) {
					tiers++;
				}else {
					tiers = 0;
				}
				int prevTier = pcTier;
				boolean notHigherTier = true;
				for(int j = 1; j < tier.length; j++) {
					if(tier[j] >= (prevTier+1) && notHigherTier) {
						pcTier = tier[j];
					}else {
						notHigherTier = false;
						pcTier = prevTier;
					}
				}
			}			
		}
		
		//Stock  market stuff
		for(JButton button : buySell) {
			if(e.getSource() == button) {
				for(int i = 0; i < buySell.length; i++)  {
					try {
						boughtShares[i] = Integer.parseInt(sharesAmount[i].getText());
					}catch(Exception e1) {}
					
					double sharePrice = 0;
					if(button == buySell[i] && buySell[i].getText().equals("Buy")) {
						try {
							sharePrice = stockPrices[i] * boughtShares[i];
							if((eur-sharePrice) >= 0) {
								eur -= sharePrice;
								Integer.parseInt(sharesAmount[i].getText());
								sharesAmount[i].setText("");
								buySell[i].setText("Sell");
							}else {
								int maxShares = (int)(eur / stockPrices[i]);
								JOptionPane.showMessageDialog(market, "You can currently buy max "+maxShares+" shares!", "Not enough money", JOptionPane.ERROR_MESSAGE);
							}
						}catch(Exception e2) {
							JOptionPane.showMessageDialog(market, "Please enter amount of shares!", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}else if(button == buySell[i] && buySell[i].getText().equals("Sell")) {
						sharePrice += stockPrices[i] * boughtShares[i];
						eur += sharePrice;
						boughtShares[i] = 0;
						buySell[i].setText("Buy");
					}
					boughtSharesL[i].setText("Shares: "+boughtShares[i]);
				}
			}
		}
		/////
		//Updates eur difference +/-
		if(eur != oldEur) {
			eurDifference.setVisible(true);
			double difference = (eur > oldEur) ? eur-oldEur : oldEur-eur;
			String mop = (eur > oldEur) ? "+" : "-";
			String color = (eur > oldEur) ? "green" : "red";
			eurDifference.setText("<html><font color="+color+"'>"+String.format(mop+"%.2f€", difference)+"</font></html>");
		}
		////
		if(tier[0] == 0) {pcTier = 0;}else buttons[0].setText("PC Case");
		
		pcOnline = (tiers >= 7) ? true : false;
		//
		generateParts(false);
		disableButtons();
		btcPS = pcPower;
		if(pcTier != 0) {pc1.setText("PC T"+pcTier);}
		if(pcOnline)btcPs.setText(String.format("BTC/s: %.6f", btcPS));
		clickerStrength.setText("<html>"+String.format("Clicker strength: <br>BTC/pc: %.6f", clickStrength)+"</html>");
		currentBtc.setText(String.format("BTC: %.6f", btc));
		currentEur.setText(String.format("EUR: %.2f€", eur));
		pcStatus.setText("<html>PC Status: "+((pcOnline) ? "<font color=green>Online</font></html>" : "<font color=red>Offline</font></html>"));
	}
	
	public static void disableButtons() {
		upgradeClicker.setEnabled((eur < clickerUpgradeCost) ? false : true);
		
		for(int i = 0; i < prices.length; i++) {
			if(prices[i] > eur || tier[i] != (pcTier) || tier[i] >= maxTier[i]) {
				buttons[i].setEnabled(false);
			}else {
				buttons[i].setEnabled(true);
			}
		}
	}
	
	static JFrame parts = new JFrame("PC");
	static boolean partsCreated = false;
	static int[] tier = {0, 0, 0, 0, 0, 0, 0};
	static int[] maxTier = {1, 5, 5, 5, 5 ,5, 5};
	static JLabel pcCase, motherboard, cpu, gpu, ram, storage, psu;
	static JButton buyPcCase, buyMotherboard, buyCpu, buyGpu, buyRam, buyStorage, buyPsu;
	static JButton[] buttons;
	static int casePrice = 3, motherboardPrice = 5, cpuPrice = 7, gpuPrice = 7, ramPrice = 3, storagePrice = 3, psuPrice = 2;
	static int[] prices = {casePrice, motherboardPrice, cpuPrice, gpuPrice, ramPrice, storagePrice, psuPrice};
	
	public static void generateParts(boolean open) {
		BTClicker bc = new BTClicker();
		if(!partsCreated) {
			//labels
			pcCase = new JLabel("Case: ");
			motherboard = new JLabel("Motherboard: ");
			cpu = new JLabel("Processor: ");
			gpu = new JLabel("Graphics card: ");
			ram = new JLabel("RAM: ");	
			storage = new JLabel("Storage: ");
			psu = new JLabel("Power Supply: ");
		
			//buttons
			buyPcCase = new JButton("Buy PC Case - "+casePrice+"€");
			buyMotherboard = new JButton("Buy T1 Motherboard - "+motherboardPrice+"€");
			buyCpu = new JButton("Buy T1 CPU - "+cpuPrice+"€");
			buyGpu = new JButton("Buy T1 GPU - "+gpuPrice+"€");
			buyRam = new JButton("Buy T1 RAM - "+ramPrice+"€");
			buyStorage = new JButton("Buy T1 Storage - "+storagePrice+"€");
			buyPsu = new JButton("Buy T1 PSU - "+psuPrice+"€");
		
			partsCreated = true;	
			buttons = new JButton[]{buyPcCase, buyMotherboard, buyCpu, buyGpu, buyRam, buyStorage, buyPsu};
			JLabel[] labels = {pcCase, motherboard, cpu, gpu, ram, storage, psu};
			
			for(int i = 0, y1 = 20; i < labels.length; i++, y1+=85) {
				labels[i].setBounds(20, y1, 250, 15);
				buttons[i].setBounds(20, (18+y1), 250, 40);
				parts.add(labels[i]);
				parts.add(buttons[i]);
				buttons[i].setFocusable(false);
				buttons[i].addActionListener(bc);
			}
		}
		
		String[] comps = {"PC Case", "Motherboard", "CPU", "GPU", "RAM", "Storage", "PSU"};
		for(int i = 1; i < buttons.length; i++) {
			boolean max = (tier[i] >= maxTier[i]) ? true : false;
			buttons[i].setText((max ? "" : "Buy")+" T"+(((tier[i]+1) > maxTier[i]) ? maxTier[i] : tier[i]+1)+" "
								+comps[i]+(max ? "" : " - "+prices[i]+"€"));
		}
		
		if(pcOnline && !autoMineOnline) {autoMine();}
		
		//preveri, èe je okno odprto
		if(open) {
			parts.setLayout(null);
			parts.setVisible(true);
			parts.setLocationRelativeTo(f);
			parts.setSize(310, 650);
			parts.setResizable(false);
		}
	}
	
	static boolean autoMineOnline = false;
	static double btcPS = 0;
	public static void autoMine() {
		autoMineOnline = true;
		btcPS = pcPower;
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				btc += (btcPS / 4);
				currentBtc.setText(String.format("BTC: %.6f", btc));
			}
		}, 250, 250);
	}	
	
	static boolean running = false;
	static double change = 0, currentShares;
	static double currentShares1[] = new double[3];
	static JButton max;
	
	static JLabel apple, appleShares;
	static JTextField amountOfShares;
	
	static JLabel[] shares= new JLabel[3];
	
	static double applePrice = 276.75, kadriolliPrice = 1.98, parkPrice = 15.02;
	static double[] stockPrices = {applePrice, kadriolliPrice, parkPrice};
	static double[] priceChanges = new double[3];
	
	static int[] boughtShares = new int[3];
	
	static boolean[] buy = {true, true, true};
	static JButton[] buySell = new JButton[3];
	
	static JTextField[] sharesAmount = new JTextField[3];
	static JLabel[] boughtSharesL = new JLabel[3];
	
	static String[] companyName = {"Apple", "Kadriolli d.o.o.", "Pizzeria Park"};
	
	static JFrame market;
	
	public static void stockMarket() {
		
		BTClicker bc = new BTClicker();
		market = new JFrame("Stock Market");
		
		market.setVisible(true);
		market.setSize(500, 230);
		market.setLocationRelativeTo(f);
		market.setLayout(null);
		market.setResizable(false);
		
		Random random = new Random();
		
		for(int i = 0, y = 25; i < shares.length; i++, y+=50) {
			
			//Label1 - Name+price
			shares[i] = new JLabel(String.format(companyName[i]+" stocks: %.2f€", stockPrices[i]));
			shares[i].setFont(new Font(shares[i].getName(), Font.CENTER_BASELINE, 14));
			shares[i].setBounds(20, y+9, 200, 20);
			market.add(shares[i]);
			
			//Buttons
			buySell[i] = new JButton(buy[i] ? "Buy" : "Sell");
			buySell[i].setBounds(250, y+5, 60, 30);
			buySell[i].addActionListener(bc);
			buySell[i].setFocusable(false);
			market.add(buySell[i]);
			
			//TextFields
			sharesAmount[i] = new JTextField();
			sharesAmount[i].setBounds(330, y+5, 60, 30);
			market.add(sharesAmount[i]);
			
			//Label2 - kolicina kupljenih shares
			boughtSharesL[i] = new JLabel("Shares: "+boughtShares[i]);
			boughtSharesL[i].setBounds(400, y+5, 110, 30);
			market.add(boughtSharesL[i]);
		}
		
		double priceChange[][] = new double[3][3];
		
		if(!running) {
			timer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					priceChange[0][0] = random.nextInt(4);
					priceChange[1][0] = ((double)random.nextInt(11)) / 100;
					priceChange[2][0] = random.nextInt(3);
					
					for(int i = 1, j = 10; i < priceChange.length; i++, j+=90) {
						priceChange[0][i] = random.nextInt(10);
						priceChange[0][i] /= j;
						priceChange[2][i] = random.nextInt(6);
						priceChange[2][i] /= j;
					}
					
					for(int i = 0; i < priceChange.length; i++) {
						priceChanges[i] = 0;
						for(int j = 0; j < priceChange.length; j++) {
							priceChanges[i] += priceChange[i][j];
						}
					}
					
					for(int i = 0; i < stockPrices.length; i++) {
						stockPrices[i] += (random.nextInt(2) == 1) ? priceChanges[i] : (-1 * priceChanges[i]);
						if(stockPrices[i] <= 0) stockPrices[i] = 0.01;
						shares[i].setText(String.format(companyName[i]+" stocks: %.2f€", stockPrices[i]));
					}
				}
			}, 0, 1000);
			running = true;
		}
	}
}