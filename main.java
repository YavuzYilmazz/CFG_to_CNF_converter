import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class main {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		@SuppressWarnings("rawtypes")
		List Datas = new ArrayList();

		try {
			File myObj = new File("CFG.txt");
			Scanner myReader = new Scanner(myObj, "UTF-8");
			while (myReader.hasNextLine()) {
				String input = myReader.nextLine();
				Datas.add(input);
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		System.out.println("CFG Form");
		System.out.println();
		Print(Datas);
		System.out.println("----------------------");
		System.out.println("Eliminate €");
		System.out.println();

		// B-€
		Datas = RemoveEpsilon("B", Datas);
		// A-€
		Datas = RemoveEpsilon("A", Datas);

		Print(Datas);

		System.out.println("----------------------");
		System.out.println("Eliminate unit production");
		System.out.println();

		Datas = RemoveUnit(Datas, "S", "A");
		Datas = RemoveUnit(Datas, "S", "B");
		Datas = RemoveUnit(Datas, "A", "B");
		Datas = RemoveUnit(Datas, "B", "A");
		Datas = RemoveUnit(Datas, "A", "S");
		Datas = RemoveUnit(Datas, "B", "S");

		Print(Datas);

		System.out.println("----------------------");
		System.out.println("Eliminate terminals");
		System.out.println();

		Datas = EliminateTerminals(Datas);

		Print(Datas);

		System.out.println("----------------------");
		System.out.println("Break variable strings longer than 2");
		System.out.println();
		
		Datas=LongerThan2(Datas);
		
		Print(Datas);
	}

	@SuppressWarnings("rawtypes")
	private static List LongerThan2(List datas) {
		int new_character=75;
		for (int i = 1; i < datas.size(); i++) {
			List<String> l2 = new ArrayList<>(Arrays.asList(datas.get(i).toString().split("-")));
			List<String> l3 = new ArrayList<>(Arrays.asList(l2.get(1).toString().split("\\|")));
			for (int j = 0; j < l3.size(); j++) {
				if(l3.get(j).length()>2)			//Find and replace those with a length of more than 2
				{
					String newTerminal=Character.toString((char) new_character);
					datas=LongerThan2ChangeAll(datas,l3.get(j),newTerminal);
					int index=findindex(datas);
					datas.add(datas.size()-index,newTerminal+"-"+l3.get(j).charAt(0)+l3.get(j).charAt(1));
					new_character++;
				}
			}
		}
		return datas;
	}
	


	private static int findindex(List datas) {		//a small function to print properly
		int counter=0;
		for (int i = 0; i < datas.get(0).toString().length(); i++) {
			if (datas.get(0).toString().charAt(i) == 'E' || datas.get(0).toString().charAt(i) == '='
					|| datas.get(0).toString().charAt(i) == ',' || datas.get(0).toString().charAt(i) == ' ') {
				continue;
			} else {
				counter++;
			}
		}
		return counter;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })//make the parts changed in the last step for all
	private static List LongerThan2ChangeAll(List datas, String OldString, String newTerminal) {
		for (int i = 1; i < datas.size(); i++) {
			datas.set(i, datas.get(i).toString().replaceAll(Character.toString(OldString.charAt(0))+Character.toString(OldString.charAt(1)), newTerminal));
		}
		return datas;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List EliminateTerminals(List datas) {
		List Terminals = new ArrayList();
		int a = 90;
		boolean flag = false;
		for (int i = 0; i < datas.get(0).toString().length(); i++) {

			// char x=datas.get(0).toString().charAt(i);
			if (datas.get(0).toString().charAt(i) == 'E' || datas.get(0).toString().charAt(i) == '='
					|| datas.get(0).toString().charAt(i) == ',' || datas.get(0).toString().charAt(i) == ' ') {
				continue;
			} else {
				Terminals.add(Character.toString(datas.get(0).toString().charAt(i)));
			}
		}

		for (int j = 0; j < Terminals.size(); j++) {
			for (int i = 1; i < datas.size(); i++) {
				if (datas.get(i).toString().contains(Terminals.get(j).toString())) {
					flag = true;
					String x = datas.get(i).toString().replaceAll(Terminals.get(j).toString(),
							Character.toString((char) a));
//					x=datas.get(i).toString().replaceAll("\\|"+Character.toString((char)a)+"\\|","\\|"+Terminals.get(j).toString()+"\\|");
//					x=datas.get(i).toString().replaceAll("\\|"+Character.toString((char)a)+"","\\|"+Terminals.get(j).toString()+"");
					String[] b = x.split("-");
					String[] c = b[1].split("\\|");
					for (int k = 0; k < c.length; k++) {
						if (c[k].equals(Character.toString((char) a))) {
							c[k] = Terminals.get(j).toString();
						}
					}
					String new_version = "";
					new_version = b[0] + "-";
					for (int k = 0; k < c.length; k++) {
						if (k == 0)
							new_version = new_version + c[k];
						else
							new_version = new_version + "|" + c[k];
					}
					datas.set(i, new_version);

				}
			}
			if (flag == true) {
				Terminals.set(j, (char) a + "-" + Terminals.get(j));
				a--;
				flag = false;

			}

		}

		for (int i = 0; i < Terminals.size(); i++) {
			datas.add(Terminals.get(i));
		}

		return datas;
	}

	@SuppressWarnings("rawtypes")
	private static void Print(List Datas) {//print function
		for (int i = 1; i < Datas.size(); i++) {
			System.out.println(Datas.get(i));
		}
		System.out.println();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List RemoveUnit(List datas, String lefthand, String righthand) {
		List<ArrayList<String>> items = new ArrayList<ArrayList<String>>(datas.size());
		for (int i = 0; i < datas.size(); i++) {
			items.add(new ArrayList<String>());
		}

		for (int i = 1; i < datas.size(); i++) {
			String[] unit1 = datas.get(i).toString().split("-");
			String[] unit2 = unit1[1].split("\\|");

			// ArrayList<String> a2 = new ArrayList<String>();
			items.get(i - 1).add(unit1[0]);
			for (int j = 0; j < unit2.length; j++) {

				items.get(i - 1).add(unit2[j]);
				// items.get(unit1[0]).add(datas.get(i).toString());
			}

		}

		// control part

		for (int i = 0; i < items.size(); i++) {
			for (int j = 0; j < items.get(i).size(); j++) {
				if (items.get(i).get(0).equals(lefthand)) {
					if (items.get(i).get(j).equals(righthand)) {
						items = Changing(items, i, j, righthand);
					}
				}
			}
		}

		String x = "";
		for (int i = 0; i < items.size() - 1; i++) {
			x = items.get(i).get(0) + "-";
			for (int j = 1; j < items.get(i).size(); j++) {
				if (j == 1)
					x = x + items.get(i).get(j);
				else
					x = x + "|" + items.get(i).get(j);
			}
			datas.set(i + 1, x);
		}
		return datas;
	}

	private static List<ArrayList<String>> Changing(List<ArrayList<String>> items, int i, int j, String righthand) {//Function responsible for making changes in part 2
		boolean flag = true;
		for (int k = 0; k < items.size() - 1; k++) {
			if (items.get(k).get(0).equals(righthand)) {
				for (int k2 = 1; k2 < items.get(k).size(); k2++) {
					if (flag == true) {
						items.get(i).set(j, items.get(k).get(k2));
						j++;
						flag = false;
					} else {
						items.get(i).add(j, items.get(k).get(k2));
						j++;
					}

				}
			}
		}
		return items;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List RemoveEpsilon(String letter, List datas) {
		List Tempdata = new ArrayList();

		for (int i = 1; i < datas.size(); i++) {
			List<String> l2 = new ArrayList<>(Arrays.asList(datas.get(i).toString().split("-")));
			List<String> l3 = new ArrayList<>(Arrays.asList(l2.get(1).toString().split("\\|")));

			for (int j = 0; j < l3.size(); j++) {

				if (l3.get(j).contains(letter)) {
					if (l3.get(j).length() == 3) {
						if (Character.toString(l3.get(j).charAt(0)).equals(letter)
								&& Character.toString(l3.get(j).charAt(1)).equals(letter)
								&& Character.toString(l3.get(j).charAt(2)).equals(letter)) {
							Tempdata.add(letter + letter);
							Tempdata.add(letter);
						} else if (Character.toString(l3.get(j).charAt(0)).equals(letter)
								&& Character.toString(l3.get(j).charAt(1)).equals(letter)) {
							Tempdata.add(letter + Character.toString(l3.get(j).charAt(2)));
							Tempdata.add(letter + Character.toString(l3.get(j).charAt(2)));
							Tempdata.add(Character.toString(l3.get(j).charAt(2)));
						} else if (Character.toString(l3.get(j).charAt(0)).equals(letter)
								&& Character.toString(l3.get(j).charAt(2)).equals(letter)) {
							Tempdata.add(Character.toString(l3.get(j).charAt(1)) + letter);
							Tempdata.add(letter + Character.toString(l3.get(j).charAt(1)));
							Tempdata.add(Character.toString(l3.get(j).charAt(1)));
						} else if (Character.toString(l3.get(j).charAt(1)).equals(letter)
								&& Character.toString(l3.get(j).charAt(2)).equals(letter)) {
							Tempdata.add(Character.toString(l3.get(j).charAt(0)) + letter);
							Tempdata.add(Character.toString(l3.get(j).charAt(0)) + letter);
							Tempdata.add(Character.toString(l3.get(j).charAt(0)));
						} else {
							Tempdata.add(l3.get(j).replaceFirst(letter, ""));
						}
					} else if (l3.get(j).length() == 2) {
						if (Character.toString(l3.get(j).charAt(0)).equals(letter)
								&& Character.toString(l3.get(j).charAt(1)).equals(letter)) {
							Tempdata.add(Character.toString(l3.get(j).charAt(0)));
						} else {
							if (Character.toString(l3.get(j).charAt(0)).equals(letter))
								Tempdata.add(Character.toString(l3.get(j).charAt(1)));
							else
								Tempdata.add(Character.toString(l3.get(j).charAt(0)));
						}
					}

				} else if (l3.get(j).equals("€"))
					l3.remove(j);
			}
			String x = "";
			String y = "";
			for (int k = 0; k < Tempdata.size(); k++) {
				x += "|" + Tempdata.get(k);
			}
			Tempdata.clear();
			if (x != null) {
				datas.set(i, datas.get(i) + x);
				y = (String) datas.get(i);
				y = y.replaceAll("€\\|", "");
				// y = y.replaceAll("-" + letter + "\\|", "-");
				// y = y.replaceAll("\\|" + letter + "\\|", "|");
				datas.set(i, y);
			}

		}

		return datas;
	}
}
