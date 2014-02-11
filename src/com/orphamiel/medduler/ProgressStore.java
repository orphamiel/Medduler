package com.orphamiel.medduler;

public class ProgressStore
{
	public int max;
	public int progress;
	public String filename;
	public String searchterm;
	
	/**
	 * Used for updating the search progress
	 * @param max contains the amount of files
	 * @param progress the amount of files searched
	 * @param filename the last file searched
	 * @param searchterm the term searched
	 */
	public ProgressStore(int max, int progress, String filename, String searchterm)
	{
		super();
		this.max = max;
		this.progress = progress;
		this.filename = filename;
		this.searchterm = searchterm;
	}
}