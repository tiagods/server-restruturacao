package com.tiagods.springbootfile.config;

import com.tiagods.prolink.config.Regex;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Regex.class)
public class TesterRegex {
    @Autowired
    private Regex regex;

    @Test
    public void validStructurePath(){
        Assert.assertTrue("GERAL/SAC".matches(regex.getStructure()));
    }
    @Test
    public void validStructurePathMin3(){
        Assert.assertFalse("GE".matches(regex.getStructure()));
    }
    @Test
    public void validStructurePathMax20(){
        Assert.assertFalse("GERAL/MINHAPASTAEXEPLOTESTE/SAC".matches(regex.getStructure()));
    }

    @Test
    public void validByInitNickName(){
        Assert.assertTrue("2222".matches(regex.getInitById()));
        Assert.assertTrue("2222 ".matches(regex.getInitById()));
        Assert.assertTrue("2222-".matches(regex.getInitById()));
        Assert.assertTrue("2222L ".matches(regex.getInitById()));
        Assert.assertFalse("22223".matches(regex.getInitById()));
        Assert.assertTrue("2222 - Teste".matches(regex.getInitById()));
        Assert.assertTrue("2222 Teste".matches(regex.getInitById()));
        Assert.assertTrue("2222 - 555Teste".matches(regex.getInitById()));
        Assert.assertTrue("2222 555Teste".matches(regex.getInitById()));
        Assert.assertTrue("0677 - ssaq545KADES".matches(regex.getInitById()));
        Assert.assertTrue("0677".matches(regex.getInitById()));
        Assert.assertTrue("0677 545KADES".matches(regex.getInitById()));
        Assert.assertFalse("067545KADES".matches(regex.getInitById()));
        Assert.assertFalse("06754".matches(regex.getInitById()));
    }
}
