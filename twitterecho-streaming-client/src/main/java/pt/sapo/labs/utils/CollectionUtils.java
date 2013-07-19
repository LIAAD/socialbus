package pt.sapo.labs.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import pt.sapo.labs.crawl.twitter.streaming.ConnectionPoolManager;

public class CollectionUtils {

	public static List<long[]> distributeUsersId(long[] usersIds) {

		String trackLimitValue = ConnectionPoolManager.TWITTER_USERS_LIMIT_DEFAULT;
		int trackLimit = Integer.parseInt(trackLimitValue);

		return distributeUsersId(usersIds, trackLimit);
	}


	public static  List<long[]> distributeUsersId(long[] tracks, int trackLimit) {
		// TODO Auto-generated method stub		

		int totalOfTracks = tracks.length;
		int numberOfStreams = calcNumberOfStreams(trackLimit, totalOfTracks);

		Stack trackStack = createTrackStack(tracks);

		List<long[]> distributedList = distributeTracks(trackLimit,numberOfStreams, trackStack);

		return distributedList;
	}

	private static List<long[]> distributeTracks(int trackLimit,
			int numberOfStreams, Stack trackStack) {

		List<long[]> distributedList = new ArrayList<long[]>(); 
		for(int i = 0; i < numberOfStreams; i++){
			long[] trackwords = getElements(trackStack,trackLimit);
			if(trackwords != null)
				distributedList.add(trackwords);
		}
		return distributedList;
	}

	private static Stack createTrackStack(long[] tracks) {
		Stack stack = new Stack(); 

		for (int i = 0; i < tracks.length; i++) {
			long track = tracks[i]; 
			stack.add(track);
		}

		return stack;
	}

	private static int calcNumberOfStreams(int blocksize, int trackArraySize) {
		int blocks = trackArraySize / blocksize;
		blocks++;
		return blocks;
	}

	private static long[] getElements(Stack stack, int numberOfElements){
		long[] elements = null;

		if(stack.size() < 1) {
			return elements;
		}

		if(numberOfElements > stack.size()) {
			numberOfElements = stack.size();
		}

		elements = new long[numberOfElements];

		int cont = 0;


		while(cont < numberOfElements && stack.size() > 0){
			long element = (Long) stack.pop();
			elements[cont] = element;
			cont++;
		}

		return elements;
	}
}
