package com.jiang;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import com.jiang.bolt.TestBolt01;
import com.jiang.bolt.TestBolt02;
import com.jiang.spout.TestSpout;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jiang
 * <p>
 * Create by 2018/6/1 15:45
 */
public class TestTopology {

    public static void main(String[] args) throws InvalidTopologyException , AlreadyAliveException {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(Constant.SPOUT_ID,new TestSpout());
        builder.setBolt(Constant.BOLT_ID,new TestBolt01(),1).shuffleGrouping(Constant.SPOUT_ID);
        builder.setBolt(Constant.BOLT2_ID, new TestBolt02() , 1).shuffleGrouping(Constant.BOLT_ID);

        //remote topology
        Map<Object,Object> conf = new HashMap<>();
        //conf.put(Config.TOPOLOGY_WORKERS,2);
        //conf.put(Config.STORM_CLUSTER_MODE, "distributed");
       StormSubmitter.submitTopology("testTopology", conf, builder.createTopology());

        //local
        //LocalCluster cluster = new LocalCluster();
        //cluster.submitTopology("testTopology",conf,builder.createTopology());
    }

}
