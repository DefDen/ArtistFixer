package ArtistFixer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

public class ArtistFixer 
{
	private String path;
	private File[] files;
	private ArrayList<Mp3File> mp3Files;

	public ArtistFixer(String path)
	{
		this.path = path;
		files = (new File(path)).listFiles();
		intializeMp3Files();
	}

	private void intializeMp3Files()
	{
		mp3Files = new ArrayList<Mp3File>();
		for(File file : files)
		{
			try 
			{
				mp3Files.add(new Mp3File(path + "\\" + file.getName()));
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}

	public void setAlbumArtistToArtist()
	{
		boolean noChanges = true;
		for(int x = 0; x < mp3Files.size(); x++)
		{
			Mp3File file = mp3Files.get(x);
			ID3v2 id3v2Tag = file.getId3v2Tag();
			if(id3v2Tag.getAlbumArtist().equals("Various Artists"))
			{
				noChanges = false;
				System.out.println(id3v2Tag.getTitle() + ", " + id3v2Tag.getAlbumArtist() + " ---> " + id3v2Tag.getArtist());
				id3v2Tag.setAlbumArtist(id3v2Tag.getArtist());
				try 
				{
					file.save(path + "\\!" + file.getFilename().substring(file.getFilename().indexOf(indexOfFirstInt(file.getFilename()))));
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		}
		if(noChanges)
		{
			System.out.print("No changes made");
		}
		else
		{
			fixNames();
		}
	}
	
	private void fixNames()
	{
		files = (new File(path)).listFiles();
		ArrayList<File> replacements = new ArrayList<File>();
		for(int x = 0; x < files.length; x++)
		{
			if(files[x].getName().charAt(0) == '!')
			{
				replacements.add(files[x]);
			}
			else
			{
				if(replacements.contains(files[x]))
				{
					files[x].delete();
				}
			}
		}
		while(replacements.size() > 0)
		{
			try 
			{
				(new Mp3File(path + "\\" + replacements.get(0).getName())).save(path + "\\" + replacements.get(0).getName().substring(1));
			}
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			replacements.get(0).delete();
			replacements.remove(0);
		}
		
	}
	
	private int indexOfFirstInt(String name)
	{
		int r = name.length();
		for(int x = 0; x < 10; x++)
		{
			if(name.indexOf(x) != -1 && r > name.indexOf(x))
			{
				r = name.indexOf(x);
			}
		}
		return r;
	}

	public static void main(String[] args)
	{
		Scanner scan = new Scanner(System.in);
		System.out.print("File pathway: ");
		ArtistFixer artistFixer = new ArtistFixer(scan.nextLine());
		scan.close();
		artistFixer.setAlbumArtistToArtist();
	}
}

