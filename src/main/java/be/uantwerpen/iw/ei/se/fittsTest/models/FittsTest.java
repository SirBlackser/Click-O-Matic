package be.uantwerpen.iw.ei.se.fittsTest.models;

import be.uantwerpen.iw.ei.se.models.MyAbstractPersistable;
import be.uantwerpen.iw.ei.se.models.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 12/11/2015.
 */
@Entity
public class FittsTest extends MyAbstractPersistable<Long>
{
    private String testID;
    private String comment;

    @OneToMany
    @JoinColumn(name="TESTSTAGE_ID", referencedColumnName="ID")
    private List<FittsTestStage> testStages;

    @ManyToMany(mappedBy = "tests")
    private List<User> users;

    public FittsTest()
    {
        this.testID = "";
        this.comment = "";
        this.testStages = new ArrayList<FittsTestStage>();
    }

    public FittsTest(String testID, List<FittsTestStage> testStages)
    {
        this.testID = testID;
        this.comment = "";
        this.testStages = testStages;
    }

    public FittsTest(String testID, List<FittsTestStage> testStages, String comment)
    {
        this.testID = testID;
        this.comment = comment;
        this.testStages = testStages;
    }

    public void setTestID(String testID)
    {
        this.testID = testID;
    }

    public String getTestID()
    {
        return this.testID;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getComment()
    {
        return this.comment;
    }

    public void setTestStages(List<FittsTestStage> testStages)
    {
        this.testStages = testStages;
    }

    public List<FittsTestStage> getTestStages()
    {
        return this.testStages;
    }

    public int getNumberOfStages()
    {
        return this.testStages.size();
    }
    
    @Override
    public boolean equals(Object object)
    {
        if(this == object)
        {
            return true;
        }

        if(object == null || this.getClass() != object.getClass())
        {
            return false;
        }

        FittsTest test = (FittsTest) object;

        return this.testID.equals(test.getTestID());
    }

    //Remove first all existing links between users and this test in the database
    @PreRemove
    private void removeTestsFromUsers()
    {
        for(User user : users)
        {
            user.getTests().remove(this);
        }
    }
}
