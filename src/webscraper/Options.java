package webscraper;
public enum Options{

    //Options for the crawler
    CRAWL_SUB_SITES, //crawls only sub_sites of the main site
    CRAWL_ALL_LINKS, //crawls everything
    CRAWL_DICITIONARY, //probes subdirectories based on a given dictionary
    CRAWL_SUB_SITES_AND_SAVE_TO_DIR, //crawls sub sites and saves found sites to local dir
    CRAWL_DICTIONARY_AND_SAVE_TO_DIR //crawls based on dictionary and saves sites locally
}