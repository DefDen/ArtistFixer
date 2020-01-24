package ArtistFixer;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

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
	private JFrame window = new JFrame("ArtistFixer");
	private JLabel label;
	private JPanel panel;

	public ArtistFixer()
	{
		makeWindow();
	}

	private void setPath(String path)
	{
		this.path = path;
		files = (new File(path)).listFiles();
		intializeMp3Files();
	}

	private void makeJTextField(GridBagConstraints c)
	{
		JTextField text = new JTextField(20);
		KeyListener listener = new KeyListener()
		{
			@Override
			public void keyPressed(KeyEvent event){}
			@Override
			public void keyReleased(KeyEvent event){}
			@Override
			public void keyTyped(KeyEvent event){}
		};
		text.addKeyListener(listener);

		text.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setPath(text.getText());
				System.out.print(path);
				label.setText(setAlbumArtistToArtist());
			}
		}
				);

		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.PAGE_START;
		panel.add(text, c);
		window.add(panel);
	}
	
	private void makeWindow()
	{
		window = new JFrame("ArtistFixer");
		window.setVisible(true);
		panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		makeJTextField(c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.PAGE_END;
		panel.add(new JLabel("File Pathway: ", SwingConstants.LEFT), c);

		
		label = new JLabel("Changes made:", SwingConstants.LEFT);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.ipady = 400; 
		c.anchor = GridBagConstraints.PAGE_START;
		panel.add(label, c);
		window.pack();
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

	public String setAlbumArtistToArtist()
	{
		String r = "";
		boolean noChanges = true;
		for(int x = 0; x < mp3Files.size(); x++)
		{
			Mp3File file = mp3Files.get(x);
			ID3v2 id3v2Tag = file.getId3v2Tag();
			if(id3v2Tag.getAlbumArtist().equals("Various Artists"))
			{
				noChanges = false;
				System.out.println(id3v2Tag.getTitle() + ", " + id3v2Tag.getAlbumArtist() + " ---> " + id3v2Tag.getArtist());
				r += id3v2Tag.getTitle() + ", " + id3v2Tag.getAlbumArtist() + " ---> " + id3v2Tag.getArtist() + "\n";
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
			r += "No changes made";
		}
		else
		{
			fixNames();
		}
		label.setText(r);
		return r;
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
		ArtistFixer artistFixer = new ArtistFixer();
	}
}

