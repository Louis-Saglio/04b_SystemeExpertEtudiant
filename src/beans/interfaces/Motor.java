package beans.interfaces;

import java.util.List;

import org.jdom2.Element;

import beans.faits.FactsBase;
import beans.faits.IFact;
import beans.regles.Rule;
import beans.regles.RulesBase;
import misc.Tuple;

public class Motor {
	private FactsBase m_fDB;
	private RulesBase m_rDB;
	private HumanInterface m_ihm;
	private RulesBase m_usedRules;

	public Motor(HumanInterface ihm) {
		m_ihm = ihm;
		m_fDB=new FactsBase();
		m_rDB=new RulesBase();
		m_usedRules=new RulesBase();
	}

	public FactsBase getfDB() {
		return m_fDB;
	}

	public RulesBase getrDB() {
		return m_rDB;
	}

	public HumanInterface getIhm() {
		return m_ihm;
	}

	public int canApply(Rule r) {
		int maxLevel=-1;
		for(IFact fact:r.getPremisses()) {
			IFact foundFact=m_fDB.search(fact.name());
			if(foundFact==null) {
				// ce fait n'existe pas dans la base actuellement
				if(fact.question()!=null) {
					// on le demande à l'utilisateur et on l'ajoute à la base
					foundFact=FactFactory.fact(fact, this);
					m_fDB.add(foundFact);
					maxLevel=Math.max(maxLevel, 0);
				}
				else {
					// on sait que la règle ne s'applique pas
					return -1;
				}
			}
			// on a un fait dans la base. On vérifie sa valeur
			if(!foundFact.value().equals(fact.value())) {
				// ne correspond pas
				return -1;
			}
			else {
				// correspond
				maxLevel=Math.max(maxLevel, foundFact.level());
			}
		}
		return maxLevel;
	}

	private Tuple findUsableRule(RulesBase rBase) {
		for(Rule r:rBase) {
			int level=canApply(r);
			if(level!=-1) {
				//Parameters.print("Trouvé la règle "+r,0);
				return new Tuple(r,level);
			}
		}
		return null;
	}

	public void solve() {
		// on fait une copie des règles existantes + création bdf vierge
		RulesBase usableRules=new RulesBase(m_rDB);
		//System.out.println("On travaille sur "+usableRules.size()+" règles");
		m_fDB.clear();
		m_usedRules.clear();

		while(true) {
			// on cherche une règle
			Tuple tupl=findUsableRule(usableRules);
			//System.out.println("Choix de la règle "+tupl.r.getName());
			if(tupl==null)
				break;
			// On applique la règle et on ajoute le nouveau fait à la base
			IFact newFact=tupl.r.getConclusion();
			newFact.setLevel(tupl.l+1);
			m_fDB.add(newFact);
			// on enlève la règle des règles applicables
			usableRules.remove(tupl.r);
			// on l'ajoute aux règles utilisées
			m_usedRules.add(tupl.r);
		}
		System.out.println(m_ihm.printFacts(m_fDB));
	}
	public void addRule(Element eRule) {
		String nom=eRule.getAttributeValue("nom");
		// les prémisses
		FactsBase premisses=new FactsBase();
		List<Element> lPrems=eRule.getChild("premisses").getChildren();
		for(Element ePrem:lPrems) {
			IFact fact=FactFactory.fact(ePrem);
			premisses.add(fact);
		}
		// la conclusion
		Element eConcl=eRule.getChild("conclusion");
		IFact concl=FactFactory.fact(eConcl);
		m_rDB.add(new Rule(premisses,concl, nom));
	}
}
