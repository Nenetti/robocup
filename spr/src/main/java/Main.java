/*
 /*
 * Copyright (C) 2014 ubuntu.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


import java.util.ArrayList;
import java.util.List;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

import process.Terminal;
import ros.ServiceClient;

/**
 * A simple {@link Subscriber} {@link NodeMain}.
 */
public class Main extends AbstractNodeMain {

	private final static String HOME=System.getProperty("user.home");

	private ServiceClient<std_msgs.String> client;
	private ServiceClient<std_msgs.String> client_jp;
	private ServiceClient<std_msgs.Float64> client_rotate;

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("spr");
	}

	@Override
	public void onStart(ConnectedNode connectedNode) {
		this.client=new ServiceClient<>(connectedNode, "sound/voice/speak_en", std_msgs.String._TYPE);
		this.client_jp=new ServiceClient<>(connectedNode, "sound/voice/speak_jp", std_msgs.String._TYPE);
		this.client_rotate=new ServiceClient<>(connectedNode, "navigation/rotate", std_msgs.Float64._TYPE);
		duration(1000);
		startSPR();
	}

	public void startSPR() {
		Terminal.execute("roslaunch turtlebot_bringup minimal.launch", false, true);
		Terminal.execute("bash "+HOME+"/catkin_ws_java/navigation.sh", false, true);
		Terminal.execute("bash "+HOME+"/catkin_ws_java/status.sh", false, true);
		Terminal.execute("bash "+HOME+"/catkin_ws_java/voice_speak.sh", false, true);
		duration(4000);
		client.publish("I want to play riddle game");
		client.waitForServer();
		client_jp.publish("日本語訳 リドルゲームしたい");
		client_jp.waitForServer();
		duration(5000);
		
		//ここで180度回転させる
		client_rotate.publish(0.0);
		client_rotate.waitForServer();
		
		duration(1000);
		client.publish("Sorry I can't Gender Recognition");
		client.waitForServer();
		client_jp.publish("日本語訳 男女認識はまだ出来てません");
		client_jp.waitForServer();
		client.publish("start riddle game");
		client.waitForServer();
		startRiddleGame();
	}

	public void startRiddleGame() {
		try {
			Terminal.execute("bash "+HOME+"/catkin_ws_java/voice_recognition.sh", true, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void duration(int time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}