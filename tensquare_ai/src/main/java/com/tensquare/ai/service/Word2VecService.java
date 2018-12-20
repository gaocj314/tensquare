package com.tensquare.ai.service;

import util.FileUtil;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 把分词文本内容训练成机器符号
 * Created by lgh on 2018/3/28.
 */
@Service
public class Word2VecService {

    //模型分词路径
    @Value("${ai.wordLib}")
    private String wordLib;

    //模型训练保存的路径
    @Value("${ai.vecModel}")
    private String vecModel;

    //爬虫分词后的数据路径
    @Value("${ai.dataPath}")
    private String dataPath;


    /**
     * 合并
     */
    public void mergeWord() {
        List<String> files = FileUtil.getFiles(dataPath);
        try {
            FileUtil.merge(wordLib, files);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据分词模型生成 词向量模型
     */
    public boolean build() {
        boolean flag = false;
        try {
            //加载数爬虫分词数据集
            SentenceIterator iter = new LineSentenceIterator(new File(wordLib));
            Word2Vec vec = new Word2Vec.Builder()
                    .minWordFrequency(5)//文本出现的次数
                    .iterations(1)//学习的迭代次数
                    .layerSize(100) //词向量的维度
                    .seed(42) //随机种子
                    .windowSize(5) //关联词之间的遗距离
                    .iterate(iter) //指定训练数据集
                    .build();
            vec.fit(); //开始训练
            //保存模型之前先删除
            new File(vecModel).delete();//删除
            WordVectorSerializer.writeWordVectors(vec, vecModel);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
