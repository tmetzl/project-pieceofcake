package maas;

import java.util.List;
import java.util.Vector;

public class Start {
	
	private List<String> agents;
	
	public Start() {
		agents = new Vector<>();
	}
	
	public void run() {
		agents.add("tester:maas.BookBuyerAgent");
    	agents.add("person:maas.CustomerAgent");
    	agents.add("baker:maas.OrderAgent");

    	List<String> cmd = new Vector<>();
    	cmd.add("-agents");
    	StringBuilder sb = new StringBuilder();
    	for (String a : agents) {
    		sb.append(a);
    		sb.append(";");
    	}
    	cmd.add(sb.toString());
    	String[] args = cmd.toArray(new String[cmd.size()]);
    	for (String s : args)
    		System.out.println(s);
        jade.Boot.main(cmd.toArray(new String[cmd.size()]));
        
        
		try {
			Thread.sleep(10000);
			String[] newArgs = {"-agents", "test3:maas.BookBuyerAgent;"};
			jade.Boot.main(newArgs);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
    public static void main(String[] args) {
    	new Start().run();
    }
}
