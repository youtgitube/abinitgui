/*
 AbinitGUI - Created in 2009
 
 Copyright (c) 2009-2015 Flavio Miguel ABREU ARAUJO (abreuaraujo.flavio@gmail.com)
                         Yannick GILLET (yannick.gillet@hotmail.com)

 Université catholique de Louvain, Louvain-la-Neuve, Belgium
 All rights reserved.

 AbinitGUI is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 AbinitGUI is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with AbinitGUI.  If not, see <http://www.gnu.org/licenses/>.

 For more information on the project, please see
 <http://gui.abinit.org/>.
 */
package abinitgui.parser;

import abivars.AllInputVars;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

/**
 *
 * @author yannick
 */
public class AbinitDataset implements Iterable<AbinitVariable>
{
    private HashMap<String,AbinitVariable> allData;
    private DepTree tree;
    
    public AbinitDataset()
    {
        allData = new HashMap<>();
    }
    
    public AbinitVariable getVariable(String name)
    {
        return allData.get(name);
    }
    
    public void setVariable(String name, AbinitVariable variable)
    {
        allData.put(name, variable);
    }
    
    public Iterator<AbinitVariable> iterator()
    {
        return allData.values().iterator();
    }
    
    public void evaluateAll(Evaluator evaluator) throws EvaluationException
    {
        /**LinkedList<String> varsForDim = new LinkedList<>();
        LinkedList<String> varsForVal = new LinkedList<>();
        
        varsForDim.addAll(allData.keySet());
        varsForVal.addAll(allData.keySet());
        
        // Brute force !
        // 1st set going to get all the dependencies !
        LinkedList<String> newVarsForDims = new LinkedList<>(varsForDim);
        for(String varname : newVarsForDims)
        {
            AbinitVariable abivar = allData.get(varname);
            try{
                abivar.evaluateDim(evaluator);
                if(abivar.getDims() != null)
                {
                    varsForDim.remove(varname);
                }
            }
            catch(EvaluationException exc)
            {
                System.err.println("exception encountered : "+exc);
            }
        }

        LinkedList<String> newVarsForVal = new LinkedList<>(varsForVal);
        for(String varname : newVarsForVal)
        {
            AbinitVariable abivar = allData.get(varname);
            try{
                abivar.evaluateValue(evaluator);
                if(abivar.getValue() != null)
                {
                    Object obj = abivar.getValue();
                    varsForVal.remove(varname);
                    // I don't want to set variable here in this new version !
                    //evaluator.putVariable(varname, obj.toString());
                }
            }
            catch(EvaluationException exc)
            {
                System.err.println("exception encoutered : "+exc);
            }
        }
        
        // At this point of the code, each variable has its dependencies:
        // TODO: Extract dependencies from the doc directly !
        this.setTree(new DepTree());
        for(AbinitVariable abivar : allData.values())
        {
            System.out.println("Dependencies of "+abivar.getDocVariable().getVarname()+" : "+abivar.getListDeps());
            getTree().addDep(abivar.getDocVariable().getVarname(), abivar.getListDeps());
        }
        
        System.out.println(getTree().toString());**/
        /**
        int maxnbiter = 10;
        while(maxnbiter > 0 && (varsForDim.size() > 0 || varsForVal.size() > 0))
        {
            newVarsForDims = new LinkedList<>(varsForDim);
            for(String varname : newVarsForDims)
            {
                AbinitVariable abivar = allData.get(varname);
                try{
                    abivar.evaluateDim(evaluator);
                    if(abivar.getDims() != null)
                    {
                        varsForDim.remove(varname);
                    }
                }
                catch(EvaluationException exc)
                {
                    System.err.println("exception encountered : "+exc);
                }
            }
            
            newVarsForVal = new LinkedList<>(varsForVal);
            for(String varname : newVarsForVal)
            {
                AbinitVariable abivar = allData.get(varname);
                try{
                    abivar.evaluateValue(evaluator);
                    if(abivar.getValue() != null)
                    {
                        Object obj = abivar.getValue();
                        varsForVal.remove(varname);
                        evaluator.putVariable(varname, obj.toString());
                    }
                }
                catch(EvaluationException exc)
                {
                    System.err.println("exception encoutered : "+exc);
                }
            }
            
            //System.out.println("evaluator = "+evaluator.getVariables());
            
            maxnbiter--;
        }
        
        if(maxnbiter == 0)
        {
            System.err.println("Number max of iteration reached !");
            System.err.println("varsForDims = "+varsForDim);
            System.err.println("varsForVal = "+varsForVal);
        }**/
    }

    /**
     * @return the tree
     */
    public DepTree getTree() {
        return tree;
    }

    /**
     * @param tree the tree to set
     */
    public void setTree(DepTree tree) {
        this.tree = tree;
    }
}