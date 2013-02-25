while line = $stdin.gets
  line.chomp!
  x = line.split(" ")
  id = 1

  x.each do |t|
    if /^(.+)_(.+)$/ =~ t
      buffer = []
      buffer << id 
      buffer << $1
      buffer << "_"
      buffer << $2 
      buffer << $2 
      buffer << "_"
      buffer << "_"
      puts buffer.join("\t")
    else
      raise Exception, "Invalid Format";
    end
    id += 1 
  end
  puts 
end
