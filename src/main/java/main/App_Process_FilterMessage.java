package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

import org.annolab.tt4j.DefaultModelResolver;
import org.annolab.tt4j.Model;
import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerException;
import org.annolab.tt4j.TreeTaggerWrapper;

import com.twitter.Extractor;

public class App_Process_FilterMessage {
	
	private static TreeTaggerWrapper<String> treeTaggerWrapper = new TreeTaggerWrapper<String>();
	private static Tokenizer tokenizer;
	private static Model treeTaggerModel;
	// counters
	private static int tokensCount = 0;
	private static int tweetCount = 0;
	
	// based on Tree Tagger Tag Set (link : https://courses.washington.edu/hypertxt/csar-v02/penntable.html)
	private static final String[] verbsPosTags = {"VB","VBD","VBG","VBN","VBZ","VBP","VD","VDD","VDG","VDN","VDZ","VDP","VH","VHD","VHG","VHN","VHZ","VHP","VV","VVD","VVG","VVN","VVP","VVZ"};
	private static final String[] whPosTags = {"WDT","WP","WRB"}; // wh-determiner (ex:which ), wh-pronoun (ex:who, what), possessive wh-pronoun (ex: whose), wh-adverbe (ex: where, when)
	private static final String[] generalJoinerPosTags = {":"}; // ex : ;, -, --
	private static final String[] currencySymbolsPosTags = {"$"}; // currency symbol ex : $, £
	private static final String[] interjectionsPosTags = {"UH"}; // interjection, ex : uhhuhhuhh
	private static final String[] toPosTags = {"TO"}; // to, ex : to go, to him ...
	private static final String[] symPosTags = {"SYM"}; // symbol, ex : @, +, *, ^, |, =
	private static final String[] endPonctuationPosTags = {"SENT"}; // end poncutation, ex : ?, !, .
	private static final String[] adverdsAndParticlesPosTags = {"RB","RBR","RBS","RP"}; // Particles are the adverb, the preposition, the conjunction and the interjection
	private static final String[] pronounsPosTags = {"PP"}; // Possessive pronoun, personal pronoun
	private static final String[] possessiveEndingPosTags = {"POS"}; //predeterminer ex : both the boys
	private static final String[] perderminersPosTags = {"PDT"}; //predeterminer ex : both the boys
	private static final String[] properNounsPosTags = {"NP","NPS"}; // ex : Jhon, Vikings
	private static final String[] nounsPosTags = {"NN","NNS"}; // ex : table ...   
	private static final String[] modalsPostags = {"MD"}; // ex : could, will
	private static final String[] adjectivessPostags = {"JJ","JJS","JJR"}; // ex : could, will
	private static final String[] complementizersPostags = {"IN/that"}; // ex : could, will
	private static final String[] prepositionsPostags = {"IN"}; // ex : could, will
	private static final String[] foreignWordsPostags = {"FW"}; // ex : could, will
	private static final String[] existentialTherePostags = {"EX"}; // ex : there is
	private static final String[] determinersPostags = {"DT"}; // ex : the
	private static final String[] cadinalNumbersPostags = {"CD"}; // ex : 1; three
	private static final String[] coordinatingConjunctionsPostags = {"CC"}; // ex : and, but, or, &
	private static final String[] othersPostags = {"(",")",",","LS","''","#"}; // virgule, parenthèse ouverte, parenthèse fermée, list marker (ex: '(1)' )  
	
	private static HashMap<String,String[]> posTypesList = new HashMap<String,String[]>() {{
			 put("verbs",verbsPosTags);
			 put("wh",whPosTags);
			 put("generalJoiner",generalJoinerPosTags);
			 put("currencySymbols",currencySymbolsPosTags);
			 put("interjections",interjectionsPosTags);
			 put("to",toPosTags);
			 put("sym",symPosTags);
			 put("endPonctuation",endPonctuationPosTags);
			 put("adverdsAndParticles",adverdsAndParticlesPosTags);
			 put("pronouns",pronounsPosTags);
			 put("possessiveEnding",possessiveEndingPosTags);
			 put("perderminers",perderminersPosTags);
			 put("properNouns",properNounsPosTags);
			 put("nouns",nounsPosTags);
			 put("modals",modalsPostags);
			 put("adjectivess",adjectivessPostags);
			 put("prepositions",prepositionsPostags);
			 put("complementizers",complementizersPostags);
			 put("complementizers",foreignWordsPostags);
			 put("existentialThere",existentialTherePostags);
			 put("determiners",determinersPostags);
			 put("cadinalNumbers",cadinalNumbersPostags);
			 put("coordinatingConjunctions",coordinatingConjunctionsPostags);
			 put("others", othersPostags);
	}};
	private static HashMap<String,Integer> wordsCountPerPosTagtypeList = new HashMap<String,Integer>();
	
	public static void main( String[] args ) 
    {
		_init();
        
    	try {
    		String query = "SELECT message FROM tweets_of_users_with_many_tweets";
    		Connection connection  = MySQLConnection.getConnection();
	        Statement statement = connection.createStatement();
	        ResultSet resultSet = statement.executeQuery(query);
	        int stopWordsCount = 0;
	        int urlsCount = 0;
	        int userReferenceTagsCount = 0;
	        int hashtagsCount = 0;
	        
	        while (resultSet.next()) {
	        	// load data of a tweet
		        String message = resultSet.getString(1);
		        tweetCount ++;
		        System.out.println("-------------------------------------------------------------------------");
		        System.out.println(String.format("Tweet NUM (%d) , Message = '%s'", tweetCount, message));
		        // get stop list
		        /*for (final String word : new WordIterator(message)) {
		            if (StopWords.English.isStopWord(word)) {
		                System.out.println(word);
		                stopWordsCount++;
		            }
		        }*/
		        Extractor extractor = new Extractor(); // written by Twitter Inc 
		        // FIXME : the code is clean, one line only but ..... functions here are being called twice, 
		        // FIXME : not a good code as it should be :s but its just for rapid test ;P
		        // get tokens that correspond to URLss
		        urlsCount += (extractor.extractURLs(message) != null)?extractor.extractURLs(message).size():0;
		        // words that correspond to user mentions
		        userReferenceTagsCount += (extractor.extractMentionedScreennames(message) != null)?extractor.extractMentionedScreennames(message).size():0;
		        // words that correspond to hashtags
		        hashtagsCount += (extractor.extractHashtags(message) != null)?extractor.extractHashtags(message).size():0;
		        // find verbs and adjectives using TreeTagger (does have many models for many languages)
		        // FIXME 
		        processWithTreeTagger(message);
		        
		        System.out.println("-------------------------Words counts Per Type --------------------------");
		        // show HashMap result 
		        int totalWordsCount = 0;
		        for(Map.Entry<String,Integer> wordsCountOfWordsType : wordsCountPerPosTagtypeList.entrySet()){
		        	System.out.println(String.format("%-40s :   %10d",wordsCountOfWordsType.getKey(),wordsCountOfWordsType.getValue()));
		        	totalWordsCount += wordsCountOfWordsType.getValue();
		        }
		        System.out.println(String.format("%-40s :   %10d",new String("Total"),totalWordsCount));
		        System.out.println("-------------------------------------------------------------------------");
		        System.out.println(String.format("%-40s :   %10d",new String("Tokens count"),tokensCount));
		        System.out.println("NOTE : Should be greater than or equal to the total of the counts per POS tags,");
		        System.out.println("	   when parsed tokens count is equal to the total of known token this means that all token have a correspondant TAG ");
		        
	        }
	        
	        System.out.println("--------------------------------------------------------------------------");
	        System.out.println("-------------------------------- RESULTS ---------------------------------");
	        System.out.println("--------------------------------------------------------------------------");
	        System.out.println("Number of stop words found    : " + stopWordsCount);
	        System.out.println("Number of urls found          : " + urlsCount);
	        System.out.println("Number of user mentions found : " + userReferenceTagsCount);
	        System.out.println("Number of hashtags found      : " + hashtagsCount);
	        connection.close();
    	} catch (SQLException e) {
	        e.printStackTrace();
	    }
  
    }
	
	private static void _init(){
		System.setProperty("treetagger.home", "data/TreeTagger");
		
		// listener of token (handler of token)
		treeTaggerWrapper.setHandler(new TokenHandler<String>() {
            public void token(String token, String pos, String lemma) {
            	for(Map.Entry<String,String[]> posType:posTypesList.entrySet()){
            		String wordsType = posType.getKey();
            		String[] wordsTypeTags = posType.getValue();
            		for(String posTags: wordsTypeTags){
            			if(pos.startsWith(posTags)){
            				Integer countForWordsType = wordsCountPerPosTagtypeList.get(wordsType);
            				countForWordsType = (countForWordsType != null)?++countForWordsType:1; 
            				wordsCountPerPosTagtypeList.put(wordsType,countForWordsType);
            				return; // Important : skip to next word type to avoid that the word will be counted more than one time for this type.	
            			}
            		}
            	}
            	System.out.println(String.format("Token didn't match any POSTag : {token= '%s' , POSTag = '%s' }",token, pos) );
            	//System.out.println(token+"\t"+pos+"\t"+lemma);
            }
        });
		
		TokenizerModel model;
		try {
			// treeTagger model load
			treeTaggerModel =new DefaultModelResolver().getModel("data/TreeTagger/model/english-utf8.par:iso8859-1");
			// openNLP tokenizer
			model = new TokenizerModel(new FileInputStream("data/open-nlp/en-token.bin"));
			tokenizer = new TokenizerME(model);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
	}
	
	private static void processWithTreeTagger(String text){
        try {
        	// XXX : should be reset before every use, or may cause problems
            treeTaggerWrapper.setModel(treeTaggerModel);
        	String tokenizedString[] = tokenizer.tokenize(text);
        	tokensCount += tokenizedString.length;
            treeTaggerWrapper.process(tokenizedString);
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TreeTaggerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally {
            treeTaggerWrapper.destroy();
        }
	}
}
