package io.sporkpgm.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileUtil {

	private static boolean deleteDirectory(File directory) {
		if(directory.exists()) {
			File[] files = directory.listFiles();
			if(null != files) {
				for(File file : files) {
					if(file.isDirectory()) {
						deleteDirectory(file);
					} else {
						file.delete();
					}
				}
			}
		}
		return directory.delete();
	}

	public static void clean() {
		File dir = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath());
		String[] folders = dir.list();
		for(String folder : folders) {
			if(folder.contains("match-")) {
				Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
				for(Player pl : players) {
					if(pl.getBedSpawnLocation() != null) {
						pl.teleport(pl.getBedSpawnLocation());
					} else {
						Location loc = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
						pl.teleport(loc);
					}
				}
				Bukkit.getServer().unloadWorld(folder, true);
				File folderfile = new File(folder);
				deleteDirectory(folderfile);
			}
		}
	}

	private static List<File> getFiles(File folder) {
		if(!folder.exists())
			folder.mkdirs();
		List<File> list = new ArrayList<>();
		for(File fileEntry : folder.listFiles()) {
			if(fileEntry.isDirectory()) {
				getFiles(fileEntry);
			} else {
				list.add(fileEntry);
			}
		}
		return list;
	}

	public static void delete(File dir) {
		if(dir.isDirectory()) {
			String[] children = dir.list();
			for(String child : children) {
				delete(new File(dir, child));
			}
		}
		dir.delete();
	}

	public static void move(File from, File to) throws IOException {
		copyFolder(from, to);
		delete(from);
	}

	public static void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void copy(File src, File dest) throws IOException {
		if(!src.exists()) {
			throw new IOException("Can not find source: " + src.getAbsolutePath() + ".");
		} else if(!src.canRead()) {
			throw new IOException("No right to source: " + src.getAbsolutePath() + ".");
		}
		if(src.isDirectory()) {
			if(!dest.exists()) {
				if(!dest.mkdirs()) {
					throw new IOException("Could not create direcotry: " + dest.getAbsolutePath() + ".");
				}
			}
			String list[] = src.list();
			for(String s : list) {
				File dest1 = new File(dest, s);
				File src1 = new File(src, s);
				copy(src1, dest1);
			}
		} else {
			FileInputStream fin = null;
			FileOutputStream fout = null;
			byte[] buffer = new byte[4096];
			int bytesRead;
			try {
				fin = new FileInputStream(src);
				fout = new FileOutputStream(dest);
				while((bytesRead = fin.read(buffer)) >= 0) {
					fout.write(buffer, 0, bytesRead);
				}
			} catch(IOException e) {
				IOException wrapper = new IOException("Unable to copy file: " + src.getAbsolutePath() + "to" + dest.getAbsolutePath() + ".");
				wrapper.initCause(e);
				wrapper.setStackTrace(e.getStackTrace());
				throw wrapper;
			} finally {
				if(fin != null) {
					fin.close();
				}
				if(fout != null) {
					fout.close();
				}
			}
		}
	}

	@SuppressWarnings("resource")
	private static void copyFolder(File src, File dest) {
		if(src.isDirectory()) {
			if(!dest.exists()) {
				dest.mkdir();
			}
			String files[] = src.list();
			for(String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				copyFolder(srcFile, destFile);
			}
		} else {
			OutputStream out;
			try {
				InputStream in = new FileInputStream(src);
				out = new FileOutputStream(dest);
				byte[] buffer = new byte[1024];
				int length;
				while((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
				out.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
