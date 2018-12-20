package com.tensquare.ai.service;
import org.apache.commons.io.FileUtils;
import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
import org.deeplearning4j.iterator.LabeledSentenceProvider;
import org.deeplearning4j.iterator.provider.FileLabeledSentenceProvider;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.GlobalPoolingLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.PoolingType;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.*;
/**
 * 人工智能分类模型
 */
@Service
public class CnnService {

    //词向量模型
    @Value("${ai.vecModel}")
    private String vecModel;

    //训练模型结果保存路径
    @Value("${ai.cnnModel}")
    private String cnnModel;

    //爬虫分词后的数据路径
    @Value("${ai.dataPath}")
    private String dataPath;

    /**
     * 构建卷积模型
     * @return
     */
    public boolean build(){
        try{
            //训练模型
            int vectorSize = 300;               //向量数量
            int cnnLayerFeatureMaps = 100;      //cnn层数量
            ComputationGraphConfiguration config = new NeuralNetConfiguration.Builder()
                    .trainingWorkspaceMode(WorkspaceMode.SINGLE)  //训练空间模型
                    .inferenceWorkspaceMode(WorkspaceMode.SINGLE) //推断空间模型
                    .weightInit(WeightInit.RELU)//权重初始化
                    .activation(Activation.LEAKYRELU)//激活
                    .updater(Updater.ADAM)
                    .convolutionMode(ConvolutionMode.Same)
                    .l2(0.0001)
                    .graphBuilder()
                    .addInputs("input")
                    .addLayer("cnn3", new ConvolutionLayer.Builder()//卷积层
                            .kernelSize(3,vectorSize)//卷积区域尺寸
                            .stride(1,vectorSize)//卷积平移步幅
                            .nIn(1)
                            .nOut(cnnLayerFeatureMaps)
                            .build(), "input")
                    .addLayer("cnn4", new ConvolutionLayer.Builder()
                            .kernelSize(4,vectorSize)
                            .stride(1,vectorSize)
                            .nIn(1)
                            .nOut(cnnLayerFeatureMaps)
                            .build(), "input")
                    .addLayer("cnn5", new ConvolutionLayer.Builder()
                            .kernelSize(5,vectorSize)
                            .stride(1,vectorSize)
                            .nIn(1)
                            .nOut(cnnLayerFeatureMaps)
                            .build(), "input")
                    .addVertex("merge", new MergeVertex(), "cnn3", "cnn4", "cnn5")//全连接层
                    .addLayer("globalPool", new GlobalPoolingLayer.Builder()//池化层
                            .poolingType(PoolingType.MAX)
                            .dropOut(0.5)
                            .build(), "merge")
                    .addLayer("out", new OutputLayer.Builder()//输出层
                            .lossFunction(LossFunctions.LossFunction.MCXENT)
                            .activation(Activation.SOFTMAX)
                            .nIn(3*cnnLayerFeatureMaps)
                            .nOut(3)
                            .build(), "globalPool")
                    .setOutputs("out")
                    .build();
            ComputationGraph net = new ComputationGraph(config);
            net.init();
            //加载词向量 训练数据集
            WordVectors wordVectors = WordVectorSerializer.loadStaticModel(new File(vecModel));
            String[] childPaths={"ai","db","web"};
            DataSetIterator trainIter = getDataSetIterator(dataPath,childPaths,  wordVectors, 32, 256,  new Random(12345));
            net.setListeners(new ScoreIterationListener(100));
            //模型训练
            net.fit(trainIter);
            //保存模型之前先删除
            new File(cnnModel).delete();
            //保存模型
            ModelSerializer.writeModel(net,cnnModel,true);
            return  true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }




    /**
     *  返回训练数据集
     * @param path
     * @param childPaths
     * @param wordVectors
     * @param minibatchSize
     * @param maxSentenceLength
     * @param rng
     * @return
     */
    private  DataSetIterator getDataSetIterator(String path, String[] childPaths,  WordVectors wordVectors, int minibatchSize,
                                                      int maxSentenceLength, Random rng ){
        //词标记分类比标签
        Map<String,List<File>> reviewFilesMap = new HashMap<>();

        for( String childPath: childPaths){
            reviewFilesMap.put(childPath, Arrays.asList(new File(path+"/"+ childPath ).listFiles()));
        }
        //标记跟踪
        LabeledSentenceProvider sentenceProvider = new FileLabeledSentenceProvider(reviewFilesMap, rng);
        return new CnnSentenceDataSetIterator.Builder()
                .sentenceProvider(sentenceProvider)
                .wordVectors(wordVectors)
                .minibatchSize(minibatchSize)
                .maxSentenceLength(maxSentenceLength)
                .useNormalizedWordVectors(false)
                .build();
    }


    /**
     * 返回map集合 分类与百分比
     * @param content
     * @return
     */
    public Map textClassify(String content) {
        System.out.println("content:"+content);
        //分词
        try {
            content=util.IKUtil.split(content," ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, Double> map = new HashMap<>();
        try {
            //模型应用
            ComputationGraph model = ModelSerializer.restoreComputationGraph(cnnModel);//
            WordVectors wordVectors = WordVectorSerializer.loadStaticModel(new File(vecModel));
            String[] childPaths={"ai","db","web"};
            DataSetIterator dataSet = getDataSetIterator(dataPath,childPaths, wordVectors, 32, 256, new Random(12345));
            INDArray featuresFirstNegative = ((CnnSentenceDataSetIterator) dataSet).loadSingleSentence(content);
            INDArray predictionsFirstNegative = model.outputSingle(featuresFirstNegative);
            List<String> labels = dataSet.getLabels();

            for (int i = 0; i < labels.size(); i++) {
                map.put(labels.get(i) + "", predictionsFirstNegative.getDouble(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return map;
    }
}
