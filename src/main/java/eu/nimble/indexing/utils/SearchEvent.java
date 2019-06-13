package eu.nimble.indexing.utils;

public enum SearchEvent {

    SEARCH_ITEM("searchItem"), SEARCH_PARTY("searchCompany"), INDEX_ONTOLOGY("indexOntology"), DELETE_ONTOLOGY("deleteOntology"),
    GET_PARTY("getCompany"), GET_ITEM("getItem");

    private String activity;

    SearchEvent(String activity){
        this.activity = activity;
    }

    public String getActivity(){
        return activity;
    }

}
