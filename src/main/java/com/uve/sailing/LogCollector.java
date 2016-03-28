package com.uve.sailing;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.joda.time.DateTime;

import com.uve.model.FileNode;

public class LogCollector {
	public static final String path = "/data0/nginx/logs/pclog/loginfo"; 
	public static final String suffix = "stats_v2.txt";
	public static final Map<AsynchronousFileChannel, FileNode> map = new HashMap<AsynchronousFileChannel, FileNode>();
	
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		DateTime time = new DateTime();
		String hh = String.valueOf(time.getHourOfDay());
		Path startingDir = Paths.get(path + '/' + hh);
		List<Path> result = new LinkedList<Path>();
		Files.walkFileTree(startingDir, new FindJavaVisitor(result));
		for(Path p : result){
			AsynchronousFileChannel channel = AsynchronousFileChannel.open(p, StandardOpenOption.READ);
			FileNode node = new FileNode();
			node.setBf(ByteBuffer.allocate(10000));
			node.setCnt(null);
			node.setOffset(0);
			map.put(channel, node);
		}
		
		for(Entry<AsynchronousFileChannel, FileNode> entry : map.entrySet()){
			Future<Integer> f = entry.getKey().read(entry.getValue().getBf(), entry.getValue().getOffset());
			entry.getValue().setCnt(f);
		}
		
		for(FileNode node : map.values()){
			Integer cnt = node.getCnt().get();
			if(cnt > 0){
				node.getBf().flip();
				byte[] b = node.getBf().array();
				System.out.println(new String(b, Charset.forName("UTF-8")));
			}
		}
		
	}
	
	public static class FindJavaVisitor extends SimpleFileVisitor<Path> {
		private List<Path> result;
		public FindJavaVisitor(List<Path> result) {
			this.result = result;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
			if (file.toString().endsWith(suffix)) {
				result.add(file.getFileName());
			}
			return FileVisitResult.CONTINUE;
		}
	}
}
