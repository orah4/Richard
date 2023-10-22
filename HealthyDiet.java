/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package healthydiet;

import java.io.IOException;
import java.util.ArrayList;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
//import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 *
 * @author ORAH RICHARD
 */
public class HealthyDiet {

    public static final String TAB = "\t";
    public static final String SEPARATOR = "-------------------------------------------------------";

  
    public void generar(int modelo) throws Exception {

        double percent = 80;
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("data_2.arff");
        Instances instances = source.getDataSet();
        instances.randomize(new java.util.Random(0));

        // 80% de los datos a ser cargados a la base de casos
        int trainSize = (int) Math.round(instances.numInstances() * percent / 100);
        // 20% : datos para la prueba
        int testSize = instances.numInstances() - trainSize;
        Instances train = new Instances(instances, 0, trainSize);
        Instances test = new Instances(instances, trainSize, testSize);

        System.out.println(HealthyDiet.TAB + "Total Sample instancias: " + instances.numInstances());
        System.out.println(HealthyDiet.TAB + "Train Set: " + trainSize);
        System.out.println(HealthyDiet.TAB + "Test Set: " + testSize);

        //Obtenemos las clases en este caso seria el ultimo atributo
        //Seleccionar la posición del atributo que identifica la clase, calidad del vino {0 al 10}
        train.setClassIndex(train.numAttributes() - 1);
        //Obtiene el numero de clases 
        int numClasses = train.numClasses();
        //Imprimir las etiquetas de las clases del training dataset
        for (int i = 0; i < numClasses; i++) {
            //Get class string value using the class index
            String classValue = train.classAttribute().value(i);
            System.out.println(HealthyDiet.TAB + "Value using clase index " + i + " es " + classValue);
        }

        //Crear modelo del clasificador con el 80 % que son datos de la muestra
        NaiveBayes nb = new NaiveBayes();
        J48 jc = new J48();

       
        
        
        
        
        
        
        nb.buildClassifier(train);
        jc.buildClassifier(train);//train BD

        System.out.println(HealthyDiet.TAB + "Testing process...");
        System.out.println(HealthyDiet.SEPARATOR);

        test.setClassIndex(test.numAttributes() - 1);
        System.out.println(HealthyDiet.TAB + "Actual Class" + HealthyDiet.TAB + "NB Predicted");
        System.out.println(HealthyDiet.TAB + "===============================");
        int correctSamples = 0;

        for (int i = 0; i < test.numInstances(); i++) {
            //Retorna el valor de la clase
            double actualClass = test.instance(i).classValue();
            //Obtiene el nombre de la clase según el valor de actualClass
            String actual = test.classAttribute().value((int) actualClass);
            //obtener la instancia de la lista de prueba
            Instance newInst = test.instance(i);
            //Predecir el valor de la clase
            double predNB = 0;
            if (modelo == 0) {
                predNB = nb.classifyInstance(newInst);
            } else {
                predNB = jc.classifyInstance(newInst);
            }

            
            
            
            
            String predString = test.classAttribute().value((int) predNB);
            System.out.println(HealthyDiet.TAB + actual + HealthyDiet.TAB + predString);
            if ((int) actualClass == (int) predNB) {
                correctSamples++;
            }
        }

        double accuracy = correctSamples / (testSize * 1.0);
        if (modelo == 0) {
            System.out.println("Accuracy: " + accuracy * 100 + " %  Application model Naive Bayes");
       
        
        
        
        } else {
            System.out.println("Accuracy: " + accuracy * 100 + " %  Application model J48");
        }

    }

    public String evaluar(
            String gener,
            String days,
            //String H,
            //String W,
            //String C,
            String f,
            String p,
            String ca,
            String ve,
            String vi,
            String wa,
            String Ls,
           int modelo) throws Exception {
        Instances data;

        data = ConverterUtils.DataSource.read("data_2.arff");
        data.setClassIndex(9);

        NaiveBayes nb = new NaiveBayes();
        J48 jc = new J48();

        nb.buildClassifier(data);
        jc.buildClassifier(data);//train BD

        double pred = 0;
        Instance instance;

        if (data.numInstances() == 0) {
            throw new Exception("clasification no disponible");
        }
        instance = crearNuevaInstancia(
                gener,
                days,
                //H,
                //W,
                //C,
                f,
                p,
                ca,
                ve,
                vi,
                wa,
                Ls,
                data);
        nb.buildClassifier(data);

        if (modelo == 0) {
            pred = nb.classifyInstance(instance);
        } else {
            pred = jc.classifyInstance(instance);
        }
        String namePred = data.classAttribute().value((int) pred);

        return namePred;

    }

    //Gender,Days,AGE,Height,Weight,Calories,Fat,Protein,Cabohydrate,Vegitable,Vitamin,Water,Life_Style_Activity
    public Instance crearNuevaInstancia(
            String Gender,
            String Days,
            //String AGE,
            //String Height,
            //String Weight,
            //String Calories,
            String Fat,
            String Protein,
            String Cabohydrate,
            String Vegitable,
            String Vitamin,
            String Water,
            String Life_Style_Activity,
            Instances train) 
            throws IOException {
        
        Instance instance = new Instance(9);

        Attribute atributo = train.attribute("Gender");
        Attribute atributo1 = train.attribute("Days");
        //Attribute atributo2 = train.attribute("AGE");
       // Attribute atributo3 = train.attribute("Height");
        //Attribute atributo4 = train.attribute("Weight");
        //Attribute atributo5 = train.attribute("Calories");
        Attribute atributo2 = train.attribute("Fat");
        Attribute atributo3 = train.attribute("Protein");
        Attribute atributo4 = train.attribute("Cabohydrate");
        Attribute atributo5 = train.attribute("Vegitable");
        Attribute atributo6 = train.attribute("Vitamin");
        Attribute atributo7 = train.attribute("Water");
        Attribute atributo8 = train.attribute("Life_Style_Activity");

        instance.setValue(atributo, Gender);
        instance.setValue(atributo1, Days);
       // instance.setValue(atributo2, AGE);
       // instance.setValue(atributo3, Height);
        //instance.setValue(atributo4, Weight);
        //instance.setValue(atributo5, Calories);
        instance.setValue(atributo2, Fat);
        instance.setValue(atributo3, Protein);
        instance.setValue(atributo4, Cabohydrate);
        instance.setValue(atributo5, Vegitable);
        instance.setValue(atributo6, Vitamin);
        instance.setValue(atributo7, Water);
        instance.setValue(atributo8, Life_Style_Activity);

        instance.setDataset(train);

        return instance;
    }

}
