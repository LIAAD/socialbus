package pt.sapo.labs.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CollectionUtils {

	public static  List<String[]> distributeFilters(String[] tracks, int trackLimit) {
		// TODO Auto-generated method stub		

		int totalOfTracks = tracks.length;
		int numberOfStreams = calcNumberOfStreams(trackLimit, totalOfTracks);

		Stack trackStack = createTrackStack(tracks);

		List<String[]> distributedList = distributeTracks(trackLimit,numberOfStreams, trackStack);

		return distributedList;
	}

	private static List<String[]> distributeTracks(int trackLimit,
			int numberOfStreams, Stack trackStack) {

		List<String[]> distributedList = new ArrayList<String[]>(); 
		for(int i = 0; i < numberOfStreams; i++){
			String[] trackwords = getElements(trackStack,trackLimit);
			if(trackwords != null)
				distributedList.add(trackwords);
		}
		return distributedList;
	}

	private static Stack createTrackStack(String[] tracks) {
		Stack stack = new Stack(); 

		for (int i = 0; i < tracks.length; i++) {
			String track = tracks[i]; 
			stack.add(track);
		}

		return stack;
	}

	private static int calcNumberOfStreams(int blocksize, int trackArraySize) {
		int blocks = trackArraySize / blocksize;
		blocks++;
		return blocks;
	}

	private static String[] getElements(Stack stack, int numberOfElements){
		String[] elements = null;

		if(stack.size() < 1) {
			return elements;
		}

		if(numberOfElements > stack.size()) {
			numberOfElements = stack.size();
		}

		elements = new String[numberOfElements];

		int cont = 0;


		while(cont < numberOfElements && stack.size() > 0){
			String element = (String) stack.pop();
			elements[cont] = element;
			cont++;
		}

		return elements;
	}
}
