package model;



public class TextModel {
    private String originalText;
    private String modifiedText;



    public TextModel() {
        this.originalText = "";
        this.modifiedText = "";
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {

        this.originalText = originalText;
        System.out.println("Set Original Text method called with: " + originalText);

    }

    public String getModifiedText() {
        return modifiedText;

    }

    public void setModifiedText(String modifiedText) {

        this.modifiedText = modifiedText;
        System.out.println("Set Modified Text method called with: " + modifiedText);
    }



}